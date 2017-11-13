package com.faforever.moderatorclient.api.dto;

import com.github.jasminb.jsonapi.annotations.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@EqualsAndHashCode(of = "id")
public abstract class AbstractEntity {
    @Id
    protected String id;
    protected OffsetDateTime createTime;
    protected OffsetDateTime updateTime;
}
