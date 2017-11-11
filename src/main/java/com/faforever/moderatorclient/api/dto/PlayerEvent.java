package com.faforever.moderatorclient.api.dto;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Type("playerEvent")
public class PlayerEvent {

    @Id
    private String id;
    private int count;

    @Relationship("event")
    private Event event;
}
