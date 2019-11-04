package com.faforever.moderatorclient.api;

import com.faforever.commons.api.elide.ElideEntity;
import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@Slf4j
public class JsonApiMessageConverter extends AbstractHttpMessageConverter<Object> {
    private final ResourceConverter resourceConverter;

    public JsonApiMessageConverter(@Qualifier("defaultResourceConverter") ResourceConverter resourceConverter) {
        super(MediaType.parseMediaType("application/vnd.api+json"));
        this.resourceConverter = resourceConverter;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return ElideEntity.class.isAssignableFrom(clazz)
                || (clazz.isArray() && supports(clazz.getComponentType()));
    }

    @Override
    @SneakyThrows
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) {
        try (InputStream inputStream = inputMessage.getBody()) {
            JSONAPIDocument<?> document;
            if (clazz.isArray()) {
                document = resourceConverter.readDocumentCollection(inputStream, clazz.getComponentType());
            } else {
                document = resourceConverter.readDocument(inputMessage.getBody(), clazz);
            }

            return document.get();
        } catch (Exception e) {
            log.error("Unable to convert JsonApi message of class {}", clazz, e);
            throw e;
        }
    }

    @Override
    @SneakyThrows
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) {
        if (o instanceof Iterable) {
            outputMessage.getBody().write(resourceConverter.writeDocumentCollection(new JSONAPIDocument<Iterable<?>>((Iterable<?>) o)));
        } else {
            outputMessage.getBody().write(resourceConverter.writeDocument(new JSONAPIDocument<>(o)));
        }
    }
}
