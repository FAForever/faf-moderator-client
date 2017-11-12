package com.faforever.moderatorclient.api;

import com.faforever.moderatorclient.api.dto.LegacyAccessLevel;
import com.faforever.moderatorclient.api.dto.Player;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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
    private final RestTemplateBuilder restTemplateBuilder;
    private final HttpComponentsClientHttpRequestFactory requestFactory;
    private final String apiClientId;
    private final String apiClientSecret;
    private final String apiAccessTokenUrl;
    private final int apiMaxPageSize;
    private CountDownLatch authorizedLatch;
    private RestOperations restOperations;


    public FafApiCommunicationService(RestTemplateBuilder restTemplateBuilder,
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
                                              int apiMaxPageSize

    ) {
        this.apiClientId = apiClientId;
        this.apiClientSecret = apiClientSecret;
        this.apiAccessTokenUrl = apiAccessTokenUrl;
        this.apiMaxPageSize = apiMaxPageSize;
        authorizedLatch = new CountDownLatch(1);
        requestFactory = new HttpComponentsClientHttpRequestFactory();
        this.restTemplateBuilder = restTemplateBuilder
                .additionalMessageConverters(jsonApiMessageConverter)
                .errorHandler(jsonApiErrorHandler)
                .rootUri(apiBaseUrl);
    }

    @SneakyThrows
    private void authorize(String username, String password) {
        log.debug("Configuring OAuth2 login with user = '{}', password=[hidden]");
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
     * @return LegacyAccessLevel of the user if login was successful, else null
     */
    public LegacyAccessLevel login(String username, String password) {
        authorize(username, password);
        try {
            Player player = getOne("/me?include=lobbyGroup", Player.class);
            return player.getLobbyGroup().getAccessLevel();
        } catch (OAuth2AccessDeniedException e) {
            return null;
        }
    }

    @SneakyThrows
    public void post(ElideRouteBuilder routeBuilder, Object request, boolean bufferRequestBody) {
        authorizedLatch.await();
        requestFactory.setBufferRequestBody(bufferRequestBody);

        try {
            // Don't use Void.class here, otherwise Spring won't even try to deserialize error messages in the body
            restOperations.postForEntity(routeBuilder.build(), request, String.class);
        } finally {
            requestFactory.setBufferRequestBody(true);
        }
    }

    @SneakyThrows
    public <T> T post(ElideRouteBuilder routeBuilder, Object request, Class<T> type) {
        authorizedLatch.await();
        ResponseEntity<T> entity = restOperations.postForEntity(routeBuilder.build(), request, type);
        return entity.getBody();
    }

    @SneakyThrows
    public <T> T patch(String endpointPath, Object request, Class<T> type) {
        authorizedLatch.await();
        return restOperations.patchForObject(endpointPath, request, type);
    }

    public void delete(ElideRouteBuilder routeBuilder) {
        restOperations.delete(routeBuilder.build());
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T> T getOne(String endpointPath, Class<T> type) {
        return restOperations.getForObject(endpointPath, type, Collections.emptyMap());
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T> T getOne(String endpointPath, Class<T> type, java.util.Map<String, Serializable> params) {
        java.util.Map<String, List<String>> multiValues = params.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Collections.singletonList(String.valueOf(entry.getValue()))));

        UriComponents uriComponents = UriComponentsBuilder.fromPath(endpointPath)
                .queryParams(CollectionUtils.toMultiValueMap(multiValues))
                .build();

        authorizedLatch.await();
        return getOne(uriComponents.toUriString(), type);
    }

    public <T> List<T> getAll(ElideRouteBuilder routeBuilder) {
        return getAll(routeBuilder, Collections.emptyMap());
    }

    public <T> List<T> getAll(ElideRouteBuilder routeBuilder, java.util.Map<String, Serializable> params) {
        return getMany(routeBuilder, apiMaxPageSize, params);
    }

    @SneakyThrows
    public <T> List<T> getMany(ElideRouteBuilder routeBuilder, int count, java.util.Map<String, Serializable> params) {
        List<T> result = new LinkedList<>();
        List<T> current = null;
        int page = 1;
        while ((current == null || current.size() >= apiMaxPageSize) && result.size() < count) {
            current = getPage(routeBuilder, count, page++, params);
            result.addAll(current);
        }
        return result;
    }

    public <T> List<T> getPage(ElideRouteBuilder routeBuilder, int pageSize, int page, java.util.Map<String, Serializable> params) {
        java.util.Map<String, List<String>> multiValues = params.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Collections.singletonList(String.valueOf(entry.getValue()))));

        return getPage(routeBuilder, pageSize, page, CollectionUtils.toMultiValueMap(multiValues));
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T> List<T> getPage(ElideRouteBuilder routeBuilder, int pageSize, int page, MultiValueMap<String, String> params) {
        authorizedLatch.await();
        String route = routeBuilder
                .pageSize(pageSize)
                .pageNumber(page)
                .build();
        log.debug("Sending API request: {}", route);
        return (List<T>) restOperations.getForObject(
                route,
                List.class);
    }
}
