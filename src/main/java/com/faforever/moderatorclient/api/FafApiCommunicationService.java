package com.faforever.moderatorclient.api;

import com.faforever.commons.api.dto.LegacyAccessLevel;
import com.faforever.commons.api.dto.Player;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

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
    private RestOperations restOperations;


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

    public RestOperations getRestOperations() {
        return restOperations;
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

        restOperations = restTemplateBuilder.configure(new OAuth2RestTemplate(details));

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
            return null;
        }
    }

    @SneakyThrows
    public <T> T post(ElideRouteBuilder<T> routeBuilder, T object) {
        String url = routeBuilder.build();

        try {
            JSONAPIDocument<T> data = new JSONAPIDocument<>(object);
            String dataString = new String(resourceConverter.writeDocument(data));
            authorizedLatch.await();
            ResponseEntity<T> entity = restOperations.postForEntity(url, dataString, routeBuilder.getDtoClass());

            cycleAvoidingMappingContext.clearCache();

            return entity.getBody();
        } catch (Throwable t) {
            applicationEventPublisher.publishEvent(new FafApiFailModifyEvent(t, routeBuilder.getDtoClass(), url));
            throw t;
        }
    }

    @SneakyThrows
    public Object postRelationship(ElideRouteBuilder<?> routeBuilder, Object object) {
        String url = routeBuilder.build();

        try {
            JSONAPIDocument<?> data = new JSONAPIDocument<>(object);
            String dataString = new String(resourceConverter.writeDocument(data));
            authorizedLatch.await();
            ResponseEntity<?> entity = restOperations.postForEntity(url, dataString, routeBuilder.getDtoClass());
            cycleAvoidingMappingContext.clearCache();

            return entity.getBody();
        } catch (Throwable t) {
            applicationEventPublisher.publishEvent(new FafApiFailModifyEvent(t, routeBuilder.getDtoClass(), url));
            throw t;
        }

    }

    @SneakyThrows
    public <T> T patch(ElideRouteBuilder<T> routeBuilder, UpdateDto<T> object) {
        cycleAvoidingMappingContext.clearCache();
        String url = routeBuilder.build();

        try {
            authorizedLatch.await();
            return restOperations.exchange(url, HttpMethod.PATCH, new HttpEntity<>(object), routeBuilder.getDtoClass()).getBody();
        } catch (Throwable t) {
            applicationEventPublisher.publishEvent(new FafApiFailModifyEvent(t, routeBuilder.getDtoClass(), url));
            throw t;
        }
    }

    @SneakyThrows
    public <T> T patch(ElideRouteBuilder<T> routeBuilder, T object) {
        cycleAvoidingMappingContext.clearCache();
        String url = routeBuilder.build();

        try {
            authorizedLatch.await();
            return restOperations.patchForObject(url, object, routeBuilder.getDtoClass());
        } catch (Throwable t) {
            applicationEventPublisher.publishEvent(new FafApiFailModifyEvent(t, routeBuilder.getDtoClass(), url));
            throw t;
        }
    }

    @SneakyThrows
    public void delete(ElideRouteBuilder<?> routeBuilder) {
        String url = routeBuilder.build();

        try {
            authorizedLatch.await();
            restOperations.delete(url);
        } catch (Throwable t) {
            applicationEventPublisher.publishEvent(new FafApiFailModifyEvent(t, routeBuilder.getDtoClass(), url));
            throw t;
        }
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T> T getOne(ElideRouteBuilder<T> routeBuilder) {
        return getOne(routeBuilder.build(), routeBuilder.getDtoClass(), Collections.emptyMap());
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T> T getOne(String endpointPath, Class<T> type) {
        return getOne(endpointPath, type, Collections.emptyMap());
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T> T getOne(String endpointPath, Class<T> type, java.util.Map<String, Serializable> params) {
        cycleAvoidingMappingContext.clearCache();
        try {
            return restOperations.getForObject(endpointPath, type, params);
        } catch (Throwable t) {
            applicationEventPublisher.publishEvent(new FafApiFailGetEvent(t, type, endpointPath));
            throw t;
        }
    }

    public <T> List<T> getAll(ElideRouteBuilder<T> routeBuilder) {
        return getAll(routeBuilder, Collections.emptyMap());
    }

    public <T> List<T> getAll(ElideRouteBuilder<T> routeBuilder, java.util.Map<String, Serializable> params) {
        return getMany(routeBuilder, apiMaxResultSize, params);
    }

    @SneakyThrows
    public <T> List<T> getMany(ElideRouteBuilder<T> routeBuilder, int count, java.util.Map<String, Serializable> params) {
        List<T> result = new LinkedList<>();
        List<T> current = null;
        int page = 1;
        while ((current == null || current.size() >= apiMaxPageSize) && result.size() < count) {
            current = getPage(routeBuilder, apiMaxPageSize, page++, params);
            result.addAll(current);
        }
        return result;
    }

    public <T> List<T> getPage(ElideRouteBuilder<T> routeBuilder, int pageSize, int page, java.util.Map<String, Serializable> params) {
        java.util.Map<String, List<String>> multiValues = params.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Collections.singletonList(String.valueOf(entry.getValue()))));

        return getPage(routeBuilder, pageSize, page, CollectionUtils.toMultiValueMap(multiValues));
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T> List<T> getPage(ElideRouteBuilder<T> routeBuilder, int pageSize, int page, MultiValueMap<String, String> params) {
        authorizedLatch.await();
        String route = routeBuilder
                .pageSize(pageSize)
                .pageNumber(page)
                .build();
        cycleAvoidingMappingContext.clearCache();
        log.debug("Sending API request: {}", route);

        try {
            return (List<T>) restOperations.getForObject(
                    route,
                    List.class,
                    params);
        } catch (Throwable t) {
            applicationEventPublisher.publishEvent(new FafApiFailGetEvent(t, routeBuilder.getDtoClass(), route));
            return Collections.emptyList();
        }
    }
}
