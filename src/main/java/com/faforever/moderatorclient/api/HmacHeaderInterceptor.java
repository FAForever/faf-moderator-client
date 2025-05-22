package com.faforever.moderatorclient.api;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class HmacHeaderInterceptor implements ClientHttpRequestInterceptor {
    @Setter
    private String hmac;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (hmac != null) {
            log.debug("Adding hmac header to request");
            request.getHeaders().add("X-HMAC", hmac);
        } else {
            log.debug("No hmac header set");
        }
        return execution.execute(request, body);
    }
}
