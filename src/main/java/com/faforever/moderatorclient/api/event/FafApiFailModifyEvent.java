package com.faforever.moderatorclient.api.event;

import lombok.Value;

@Value
public class FafApiFailModifyEvent {
    private final Throwable cause;
    private final String url;
    private final Class<?> entityClass;

    public FafApiFailModifyEvent(Throwable cause, Class<?> entityClass, String url) {
        this.cause = cause;
        this.entityClass = entityClass;
        this.url = url;
    }
}
