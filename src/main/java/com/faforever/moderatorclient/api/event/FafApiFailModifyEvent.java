package com.faforever.moderatorclient.api.event;

import lombok.Value;

@Value
public class FafApiFailModifyEvent {
    Throwable cause;
    String url;
    Class<?> entityClass;

    public FafApiFailModifyEvent(Throwable cause, Class<?> entityClass, String url) {
        this.cause = cause;
        this.entityClass = entityClass;
        this.url = url;
    }
}
