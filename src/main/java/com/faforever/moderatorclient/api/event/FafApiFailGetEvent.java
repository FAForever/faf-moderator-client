package com.faforever.moderatorclient.api.event;

import lombok.Data;

@Data
public class FafApiFailGetEvent {
    private final Throwable cause;
    private final String url;
    private final Class<?> entityClass;
}
