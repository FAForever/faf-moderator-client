package com.faforever.moderatorclient.api.event;

import lombok.Value;

@Value
public class FafUserFailModifyEvent {
    Throwable cause;
    Class<?> objectClass;
    String url;
}
