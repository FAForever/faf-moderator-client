package com.faforever.moderatorclient.api;

import com.faforever.commons.api.dto.LegacyAccessLevel;
import com.faforever.commons.api.dto.Player;
import com.faforever.commons.api.elide.ElideEntity;
import com.faforever.commons.api.elide.ElideNavigator;
import com.faforever.commons.api.elide.ElideNavigatorOnCollection;
import com.faforever.commons.api.elide.ElideNavigatorOnId;
import com.faforever.moderatorclient.api.dto.UpdateDto;
import com.faforever.moderatorclient.api.event.FafApiFailGetEvent;
import com.faforever.moderatorclient.api.event.FafApiFailModifyEvent;
import com.faforever.moderatorclient.mapstruct.CycleAvoidingMappingContext;
import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FafApiCommunicationService {
    private final ResourceConverter resourceConverter;
    private final ApplicationEventPublisher applicationEventPublisher;
    @Getter
    private Player selfPlayer;
    private final CycleAvoidingMappingContext cycleAvoidingMappingContext;
    private final RestTemplateBuilder restTemplateBuilder;
    private final String apiClientId;
    private final String apiClientSecret;
    private final String apiAccessTokenUrl;
    private final int apiMaxPageSize;
    private final int apiMaxResultSize;
    private CountDownLatch authorizedLatch;
    private RestTemplate restTemplate;


    public FafApiCommunicationService(ResourceConverter resourceConverter, ApplicationEventPublisher applicationEventPublisher, CycleAvoidingMappingContext cycleAvoidingMappingContext, RestTemplateBuilder restTemplateBuilder,
                                      JsonApiMessageConverter jsonApiMessageConverter,
                                      JsonApiErrorHandler jsonApiErrorHandler,
                                      @Value("${faforever.api.base-url}")
                                              String apiBaseUrl,
                                      @Value("${faforever.api.client-id}")
                                              String apiClientId,
                                      @Value("${faforever.api.client-secret}")
                                              String apiClientSecret,
                                      @Value("${faforever.api.access-token-uri}")
                                              String apiAccessTokenUrl,
                                      @Value("${faforever.api.max-page-size}")
                                              int apiMaxPageSize,
                                      @Value("${faforever.api.max-result-size}")
                                              int apiMaxResultSize) {
        this.resourceConverter = resourceConverter;
        this.applicationEventPublisher = applicationEventPublisher;
        this.cycleAvoidingMappingContext = cycleAvoidingMappingContext;
        this.apiClientId = apiClientId;
        this.apiClientSecret = apiClientSecret;
        this.apiAccessTokenUrl = apiAccessTokenUrl;
        this.apiMaxPageSize = apiMaxPageSize;
        this.apiMaxResultSize = apiMaxResultSize;
        authorizedLatch = new CountDownLatch(1);
        this.restTemplateBuilder = restTemplateBuilder
                .additionalMessageConverters(jsonApiMessageConverter)
                .errorHandler(jsonApiErrorHandler)
                .rootUri(apiBaseUrl);
    }

    public RestOperations getRestTemplate() {
        return restTemplate;
    }

    @SneakyThrows
    private void authorize(String username, String password) {
        log.debug("Configuring OAuth2 login with player = '{}', password=[hidden]", username);
        ResourceOwnerPasswordResourceDetails details = new ResourceOwnerPasswordResourceDetails();
        details.setClientId(apiClientId);
        details.setClientSecret(apiClientSecret);
        details.setClientAuthenticationScheme(AuthenticationScheme.header);
        details.setAccessTokenUri(apiAccessTokenUrl);
        details.setUsername(username);
        details.setPassword(password);

        restTemplate = restTemplateBuilder.configure(new OAuth2RestTemplate(details));
        restTemplate.setInterceptors(Collections.singletonList(
                (request, body, execution) -> {
                    HttpHeaders headers = request.getHeaders();
                    headers.setAccept(Collections.singletonList(MediaType.valueOf("application/vnd.api+json")));
                    if (request.getMethod() == HttpMethod.POST || request.getMethod() == HttpMethod.PATCH || request.getMethod() == HttpMethod.PUT) {
                        headers.setContentType(MediaType.APPLICATION_JSON);
                    }
                    return execution.execute(request, body);
                }
        ));

        authorizedLatch.countDown();
    }

    /**
     * @return LegacyAccessLevel of the player if login was successful, else null
     */
    public LegacyAccessLevel login(String username, String password) {
        authorize(username, password);
        try {
            selfPlayer = getOne("/me?include=lobbyGroup", Player.class);
            if (selfPlayer.getLobbyGroup() == null) {
                return LegacyAccessLevel.ROLE_USER;
            }

            return selfPlayer.getLobbyGroup().getAccessLevel();
        } catch (OAuth2AccessDeniedException e) {
            log.error("login failed", e);
            return null;
        }
    }

    @SneakyThrows
    public <T extends ElideEntity> T post(ElideNavigatorOnCollection<T> navigator, T object) {
        String url = navigator.build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            JSONAPIDocument<T> data = new JSONAPIDocument<>(object);
            String dataString = new String(resourceConverter.writeDocument(data));
            authorizedLatch.await();
            HttpEntity<String> tHttpEntity = new HttpEntity<>(dataString, httpHeaders);
            ResponseEntity<T> entity = restTemplate.exchange(url, HttpMethod.POST, tHttpEntity, navigator.getDtoClass());

            cycleAvoidingMappingContext.clearCache();

            return entity.getBody();
        } catch (Throwable t) {
            applicationEventPublisher.publishEvent(new FafApiFailModifyEvent(t, navigator.getDtoClass(), url));
            throw t;
        }
    }


    @SneakyThrows
    public <T extends ElideEntity> T patch(ElideNavigatorOnId<T> routeBuilder, UpdateDto<T> object) {
        cycleAvoidingMappingContext.clearCache();
        String url = routeBuilder.build();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            authorizedLatch.await();
            return restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(object, httpHeaders), routeBuilder.getDtoClass()).getBody();
        } catch (Throwable t) {
            applicationEventPublisher.publishEvent(new FafApiFailModifyEvent(t, routeBuilder.getDtoClass(), url));
            throw t;
        }
    }

    @SneakyThrows
    public <T extends ElideEntity> T patch(ElideNavigatorOnId<T> routeBuilder, T object) {
        cycleAvoidingMappingContext.clearCache();
        String url = routeBuilder.build();

        try {
            authorizedLatch.await();
            return restTemplate.patchForObject(url, object, routeBuilder.getDtoClass());
        } catch (Throwable t) {
            applicationEventPublisher.publishEvent(new FafApiFailModifyEvent(t, routeBuilder.getDtoClass(), url));
            throw t;
        }
    }

    public <T extends ElideEntity> void delete(T entity) {
        delete(ElideNavigator.of(entity));
    }

    @SneakyThrows
    public void delete(ElideNavigatorOnId<?> navigator) {
        String url = navigator.build();

        try {
            authorizedLatch.await();
            restTemplate.delete(url);
        } catch (Throwable t) {
            applicationEventPublisher.publishEvent(new FafApiFailModifyEvent(t, navigator.getDtoClass(), url));
            throw t;
        }
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T extends ElideEntity> T getOne(ElideNavigatorOnId<T> navigator) {
        return getOne(navigator.build(), navigator.getDtoClass(), Collections.emptyMap());
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T extends ElideEntity> T getOne(String endpointPath, Class<T> type) {
        return getOne(endpointPath, type, Collections.emptyMap());
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T extends ElideEntity> T getOne(String endpointPath, Class<T> type, java.util.Map<String, Serializable> params) {
        cycleAvoidingMappingContext.clearCache();
        try {
            return restTemplate.getForObject(endpointPath, type, params);
        } catch (Throwable t) {
            applicationEventPublisher.publishEvent(new FafApiFailGetEvent(t, endpointPath, type));
            throw t;
        }
    }

    public <T extends ElideEntity> List<T> getAll(ElideNavigatorOnCollection<T> routeBuilder) {
        return getAll(routeBuilder, Collections.emptyMap());
    }

    public <T extends ElideEntity> List<T> getAll(ElideNavigatorOnCollection<T> routeBuilder, java.util.Map<String, Serializable> params) {
        return getMany(routeBuilder, apiMaxResultSize, params);
    }

    @SneakyThrows
    public <T extends ElideEntity> List<T> getMany(ElideNavigatorOnCollection<T> routeBuilder, int count, java.util.Map<String, Serializable> params) {
        List<T> result = new LinkedList<>();
        List<T> current = null;
        int page = 1;
        while ((current == null || current.size() >= apiMaxPageSize) && result.size() < count) {
            current = getPage(routeBuilder, apiMaxPageSize, page++, params);
            result.addAll(current);
        }
        return result;
    }

    public <T extends ElideEntity> List<T> getPage(ElideNavigatorOnCollection<T> routeBuilder, int pageSize, int page, java.util.Map<String, Serializable> params) {
        java.util.Map<String, List<String>> multiValues = params.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Collections.singletonList(String.valueOf(entry.getValue()))));

        return getPage(routeBuilder, pageSize, page, CollectionUtils.toMultiValueMap(multiValues));
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T extends ElideEntity> List<T> getPage(ElideNavigatorOnCollection<T> routeBuilder, int pageSize, int page, MultiValueMap<String, String> params) {
        authorizedLatch.await();
        String route = routeBuilder
                .pageSize(pageSize)
                .pageNumber(page)
                .build();
        cycleAvoidingMappingContext.clearCache();
        log.debug("Sending API request: {}", route);

        try {
            return (List<T>) restTemplate.getForObject(
                    route,
                    List.class,
                    params);
        } catch (Throwable t) {
            log.error("API returned error on getPage for route ''{}''", route, t);
            applicationEventPublisher.publishEvent(new FafApiFailGetEvent(t, route, routeBuilder.getDtoClass()));
            return Collections.emptyList();
        }
    }
}
