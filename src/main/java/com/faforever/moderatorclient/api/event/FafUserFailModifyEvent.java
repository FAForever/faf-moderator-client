package com.faforever.moderatorclient.api.event;

import lombok.Value;

@Value
public class FafUserFailModifyEvent {
    Throwable cause;
    String url;
    Class<?> objectClass;

    public FafUserFailModifyEvent(Throwable cause, Class<?> objectClass, String url) {
        this.cause = cause;
        this.objectClass = objectClass;
        this.url = url;
    }
}
