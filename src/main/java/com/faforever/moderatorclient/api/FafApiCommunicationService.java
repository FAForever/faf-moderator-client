package com.faforever.moderatorclient.api;

import com.faforever.commons.api.dto.MeResult;
import com.faforever.commons.api.elide.ElideEntity;
import com.faforever.commons.api.elide.ElideNavigator;
import com.faforever.commons.api.elide.ElideNavigatorOnCollection;
import com.faforever.commons.api.elide.ElideNavigatorOnId;
import com.faforever.commons.api.update.UpdateDto;
import com.faforever.moderatorclient.api.event.ApiAuthorizedEvent;
import com.faforever.moderatorclient.api.event.FafApiFailGetEvent;
import com.faforever.moderatorclient.api.event.FafApiFailModifyEvent;
import com.faforever.moderatorclient.api.event.HydraAuthorizedEvent;
import com.faforever.moderatorclient.config.EnvironmentProperties;
import com.faforever.moderatorclient.mapstruct.CycleAvoidingMappingContext;
import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FafApiCommunicationService {
    private final ResourceConverter defaultResourceConverter;
    private final ResourceConverter updateResourceConverter;
    private final OAuthTokenInterceptor oAuthTokenInterceptor;
    private final HmacHeaderInterceptor hmacHeaderInterceptor;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final JsonApiMessageConverter jsonApiMessageConverter;
    private final JsonApiErrorHandler jsonApiErrorHandler;
    private final CycleAvoidingMappingContext cycleAvoidingMappingContext;
    private final RestTemplateBuilder restTemplateBuilder;
    private final CountDownLatch authorizedLatch;
    @Getter
    private MeResult meResult;
    private RestTemplate restTemplate;
    private EnvironmentProperties environmentProperties;


    public FafApiCommunicationService(@Qualifier("defaultResourceConverter") ResourceConverter defaultResourceConverter,
                                      @Qualifier("updateResourceConverter") ResourceConverter updateResourceConverter,
                                      OAuthTokenInterceptor oAuthTokenInterceptor, HmacHeaderInterceptor hmacHeaderInterceptor, ApplicationEventPublisher applicationEventPublisher,
                                      CycleAvoidingMappingContext cycleAvoidingMappingContext, RestTemplateBuilder restTemplateBuilder,
                                      JsonApiMessageConverter jsonApiMessageConverter,
                                      JsonApiErrorHandler jsonApiErrorHandler) {
        this.defaultResourceConverter = defaultResourceConverter;
        this.updateResourceConverter = updateResourceConverter;
        this.hmacHeaderInterceptor = hmacHeaderInterceptor;
        this.applicationEventPublisher = applicationEventPublisher;
        this.cycleAvoidingMappingContext = cycleAvoidingMappingContext;
        this.jsonApiMessageConverter = jsonApiMessageConverter;
        this.jsonApiErrorHandler = jsonApiErrorHandler;
        this.oAuthTokenInterceptor = oAuthTokenInterceptor;
        this.restTemplateBuilder = restTemplateBuilder;

        authorizedLatch = new CountDownLatch(1);
    }

    public RestOperations getRestTemplate() {
        return restTemplate;
    }

    public void initialize(EnvironmentProperties environmentProperties) {
        this.environmentProperties = environmentProperties;
    }

    public boolean hasPermission(String... permissionTechnicalName) {
        return meResult.getPermissions().stream()
                .anyMatch(permission -> Arrays.asList(permissionTechnicalName).contains(permission));
    }

    @SneakyThrows
    @EventListener
    public void authorize(HydraAuthorizedEvent event) {
        meResult = null;

        restTemplate = restTemplateBuilder
                .requestFactory(JdkClientHttpRequestFactory.class)
                .additionalMessageConverters(jsonApiMessageConverter)
                .readTimeout(Duration.ofMinutes(5))
                .errorHandler(jsonApiErrorHandler)
                .rootUri(environmentProperties.getBaseUrl())
                .interceptors(List.of(oAuthTokenInterceptor, hmacHeaderInterceptor,
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
                )).build();

        try {
            meResult = getOne("/me", MeResult.class);
        } catch (Exception e) {
            log.error("login failed", e);
            return;
        }

        authorizedLatch.countDown();
        applicationEventPublisher.publishEvent(new ApiAuthorizedEvent());
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
        String url = navigator.build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            JSONAPIDocument<T> data = new JSONAPIDocument<>(object);
            String dataString = new String(defaultResourceConverter.writeDocument(data));
            authorizedLatch.await();
            HttpEntity<String> httpEntity = new HttpEntity<>(dataString, httpHeaders);
            ResponseEntity<T> entity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, navigator.getDtoClass());

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

    @SneakyThrows
    public <T extends ElideEntity> T getOne(ElideNavigatorOnId<T> navigator) {
        return getOne(navigator.build(), navigator.getDtoClass(), Collections.emptyMap());
    }

    @SneakyThrows
    public <T extends ElideEntity> T getOne(String endpointPath, Class<T> type) {
        return getOne(endpointPath, type, Collections.emptyMap());
    }

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
