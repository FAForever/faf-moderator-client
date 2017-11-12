package com.faforever.moderatorclient.api.dto;

import com.github.jasminb.jsonapi.annotations.Id;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public abstract class AbstractEntity {
    @Id
    protected String id;
    protected OffsetDateTime createTime;
    protected OffsetDateTime updateTime;
}
