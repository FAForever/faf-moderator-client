package com.faforever.moderatorclient.api;

import com.faforever.moderatorclient.api.event.FafUserFailModifyEvent;
import com.faforever.moderatorclient.api.event.HydraAuthorizedEvent;
import com.faforever.moderatorclient.config.EnvironmentProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
@Slf4j
public class FafUserCommunicationService {
    private final OAuthTokenInterceptor oAuthTokenInterceptor;
    private final RestTemplateBuilder restTemplateBuilder;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CountDownLatch authorizedLatch;
    private RestTemplate restTemplate;
    private EnvironmentProperties environmentProperties;


    public FafUserCommunicationService(OAuthTokenInterceptor oAuthTokenInterceptor, ApplicationEventPublisher applicationEventPublisher,
                                       RestTemplateBuilder restTemplateBuilder) {
        this.oAuthTokenInterceptor = oAuthTokenInterceptor;
        this.applicationEventPublisher = applicationEventPublisher;
        this.restTemplateBuilder = restTemplateBuilder;

        authorizedLatch = new CountDownLatch(1);
    }

    public RestOperations getRestTemplate() {
        return restTemplate;
    }

    public void initialize(EnvironmentProperties environmentProperties) {
        this.environmentProperties = environmentProperties;
    }

    @SneakyThrows
    @EventListener
    public void authorize(HydraAuthorizedEvent event) {
        restTemplate = restTemplateBuilder
                .rootUri(environmentProperties.getUserBaseUrl())
                .interceptors(List.of(oAuthTokenInterceptor,
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
        authorizedLatch.countDown();
    }

    @SneakyThrows
    public void post(String url, Object object) {
        try {
            authorizedLatch.await();
            restTemplate.postForObject(url, object, String.class);
        } catch (Throwable t) {
            applicationEventPublisher.publishEvent(new FafUserFailModifyEvent(t, object.getClass(), url));
            throw t;
        }
    }
}
