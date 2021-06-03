package com.faforever.moderatorclient.api;

import com.faforever.commons.api.dto.MeResult;
import com.faforever.commons.api.elide.ElideEntity;
import com.faforever.commons.api.elide.ElideNavigator;
import com.faforever.commons.api.elide.ElideNavigatorOnCollection;
import com.faforever.commons.api.elide.ElideNavigatorOnId;
import com.faforever.commons.api.update.UpdateDto;
import com.faforever.moderatorclient.api.event.FafApiFailGetEvent;
import com.faforever.moderatorclient.api.event.FafApiFailModifyEvent;
import com.faforever.moderatorclient.config.EnvironmentProperties;
import com.faforever.moderatorclient.mapstruct.CycleAvoidingMappingContext;
import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FafApiCommunicationService {
    private final ResourceConverter defaultResourceConverter;
    private final ResourceConverter updateResourceConverter;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final JsonApiMessageConverter jsonApiMessageConverter;
    private final JsonApiErrorHandler jsonApiErrorHandler;
    private final ApplicationContext applicationContext;
    @Getter
    private MeResult meResult;
    private final CycleAvoidingMappingContext cycleAvoidingMappingContext;
    private RestTemplateBuilder restTemplateBuilder;
    private CountDownLatch authorizedLatch;
    private RestTemplate restTemplate;
    private EnvironmentProperties environmentProperties;


    public FafApiCommunicationService(@Qualifier("defaultResourceConverter") ResourceConverter defaultResourceConverter,
                                      @Qualifier("updateResourceConverter") ResourceConverter updateResourceConverter,
                                      ApplicationEventPublisher applicationEventPublisher, CycleAvoidingMappingContext cycleAvoidingMappingContext, RestTemplateBuilder restTemplateBuilder,
                                      JsonApiMessageConverter jsonApiMessageConverter,
                                      JsonApiErrorHandler jsonApiErrorHandler,
                                      ApplicationContext applicationContext) {
        this.defaultResourceConverter = defaultResourceConverter;
        this.updateResourceConverter = updateResourceConverter;
        this.applicationEventPublisher = applicationEventPublisher;
        this.cycleAvoidingMappingContext = cycleAvoidingMappingContext;
        this.jsonApiMessageConverter = jsonApiMessageConverter;
        this.jsonApiErrorHandler = jsonApiErrorHandler;
        this.applicationContext = applicationContext;

        authorizedLatch = new CountDownLatch(1);
    }

    public RestOperations getRestTemplate() {
        return restTemplate;
    }

    public void initialize(EnvironmentProperties environmentProperties) {
        this.environmentProperties = environmentProperties;
        this.restTemplateBuilder = applicationContext.getBean(RestTemplateBuilder.class)
                .additionalMessageConverters(jsonApiMessageConverter)
                .errorHandler(jsonApiErrorHandler)
                .rootUri(environmentProperties.getBaseUrl());
    }

    public boolean hasPermission(String... permissionTechnicalName) {
        return meResult.getPermissions().stream()
                .anyMatch(permission -> Arrays.asList(permissionTechnicalName).contains(permission));
    }

    @SneakyThrows
    private void authorize(String username, String password) {
        log.debug("Configuring OAuth2 login with player = '{}', password=[hidden]", username);
        ResourceOwnerPasswordResourceDetails details = new ResourceOwnerPasswordResourceDetails();
        details.setClientId(environmentProperties.getClientId());
        details.setClientSecret(environmentProperties.getClientSecret());
        details.setClientAuthenticationScheme(AuthenticationScheme.header);
        details.setAccessTokenUri(environmentProperties.getAccessTokenUri());
        details.setUsername(username);
        details.setPassword(password);

        restTemplate = restTemplateBuilder.configure(new OAuth2RestTemplate(details));
        restTemplate.setInterceptors(Collections.singletonList(
                (request, body, execution) -> {
                    HttpHeaders headers = request.getHeaders();

                    List<String> contentTypes = headers.get(HttpHeaders.CONTENT_TYPE);
                    if (contentTypes != null && contentTypes.stream()
                            .anyMatch(MediaType.APPLICATION_JSON_VALUE::equalsIgnoreCase)) {
                        headers.setAccept(Collections.singletonList(MediaType.valueOf("application/vnd.api+json")));
                        if (request.getMethod() == HttpMethod.POST || request.getMethod() == HttpMethod.PATCH || request.getMethod() == HttpMethod.PUT) {
                            headers.setContentType(MediaType.APPLICATION_JSON);
                        }
                    }
                    return execution.execute(request, body);
                }
        ));

        authorizedLatch.countDown();
    }

    /**
     * @return MeResult of the player if login was successful, else null
     */
    public MeResult login(String username, String password) {
        authorize(username, password);
        try {
            meResult = getOne("/me", MeResult.class);
            return meResult;
        } catch (OAuth2AccessDeniedException e) {
            log.error("login failed", e);
            return null;
        }
    }

    public void forceRenameUserName(String userId, String newName) {
        String path = String.format("/users/%s/forceChangeUsername", userId);
        String url = UriComponentsBuilder.fromPath(path).queryParam("newUsername", newName).toUriString();
        try {
            restTemplate.exchange(url, HttpMethod.POST, null, Void.class, Map.of());
        } catch (Throwable t) {
            applicationEventPublisher.publishEvent(new FafApiFailModifyEvent(t, Void.class, url));
            throw t;
        }
    }

    @SneakyThrows
    public <T extends ElideEntity> T post(ElideNavigatorOnCollection<T> navigator, T object) {
        return post(navigator.build(), navigator.getDtoClass(), object);
    }

    @Nullable
    private <T, E> E post(String url, Class<E> returnType, T object) throws DocumentSerializationException, InterruptedException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            JSONAPIDocument<T> data = new JSONAPIDocument<>(object);
            String dataString = new String(defaultResourceConverter.writeDocument(data));
            authorizedLatch.await();
            HttpEntity<String> httpEntity = new HttpEntity<>(dataString, httpHeaders);
            ResponseEntity<E> entity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, returnType);

            cycleAvoidingMappingContext.clearCache();

            return entity.getBody();
        } catch (Throwable t) {
            applicationEventPublisher.publishEvent(new FafApiFailModifyEvent(t, returnType, url));
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
            JSONAPIDocument<UpdateDto<T>> data = new JSONAPIDocument<>(object);
            String dataString = new String(updateResourceConverter.writeDocument(data));
            authorizedLatch.await();
            HttpEntity<String> httpEntity = new HttpEntity<>(dataString, httpHeaders);
            return restTemplate.exchange(url, HttpMethod.PATCH, httpEntity, routeBuilder.getDtoClass()).getBody();
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

    public <T extends ElideEntity> List<T> getAll(Class<T> clazz, ElideNavigatorOnCollection<T> routeBuilder) {
        return getAll(clazz, routeBuilder, Collections.emptyMap());
    }

    public <T extends ElideEntity> List<T> getAll(Class<T> clazz, ElideNavigatorOnCollection<T> routeBuilder, java.util.Map<String, Serializable> params) {
        return getMany(clazz, routeBuilder, environmentProperties.getMaxResultSize(), params);
    }

    @SneakyThrows
    public <T extends ElideEntity> List<T> getMany(Class<T> clazz, ElideNavigatorOnCollection<T> routeBuilder, int count, java.util.Map<String, Serializable> params) {
        List<T> result = new LinkedList<>();
        List<T> current = null;
        int page = 1;
        while ((current == null || current.size() >= environmentProperties.getMaxPageSize()) && result.size() < count) {
            current = getPage(clazz, routeBuilder, environmentProperties.getMaxPageSize(), page++, params);
            result.addAll(current);
        }
        return result;
    }

    public <T extends ElideEntity> List<T> getPage(Class<T> clazz, ElideNavigatorOnCollection<T> routeBuilder, int pageSize, int page, java.util.Map<String, Serializable> params) {
        java.util.Map<String, List<String>> multiValues = params.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Collections.singletonList(String.valueOf(entry.getValue()))));

        return getPage(clazz, routeBuilder, pageSize, page, CollectionUtils.toMultiValueMap(multiValues));
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T extends ElideEntity> List<T> getPage(Class<T> clazz, ElideNavigatorOnCollection<T> routeBuilder, int pageSize, int page, MultiValueMap<String, String> params) {
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
                    Array.newInstance(clazz, 0).getClass(),
                    params);
        } catch (Throwable t) {
            log.error("API returned error on getPage for route ''{}''", route, t);
            applicationEventPublisher.publishEvent(new FafApiFailGetEvent(t, route, routeBuilder.getDtoClass()));
            return Collections.emptyList();
        }
    }
}
