package com.faforever.moderatorclient.api.event;

import lombok.Value;

@Value
public class FafApiFailModifyEvent {
    Throwable cause;
    Class<?> entityClass;
    String url;
}
