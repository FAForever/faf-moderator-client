package com.faforever.moderatorclient.api;


import com.faforever.commons.api.dto.ApiException;
import com.github.jasminb.jsonapi.exceptions.ResourceParseException;
import com.github.jasminb.jsonapi.models.errors.Errors;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.io.InputStreamReader;

@Component
@Slf4j
public class JsonApiErrorHandler extends DefaultResponseErrorHandler {
    private final JsonApiMessageConverter jsonApiMessageConverter;

    public JsonApiErrorHandler(JsonApiMessageConverter jsonApiMessageConverter) {
        this.jsonApiMessageConverter = jsonApiMessageConverter;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        log.warn("Api call returned with error code '{}' and body '{}'", response.getStatusCode(), CharStreams.toString(new InputStreamReader(response.getBody(), Charsets.UTF_8)));

        if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
            try {
                jsonApiMessageConverter.readInternal(Errors.class, response);
            } catch (ResourceParseException e) {
                throw new ApiException(e.getErrors().getErrors());
            }
        }
        super.handleError(response);
    }
}
