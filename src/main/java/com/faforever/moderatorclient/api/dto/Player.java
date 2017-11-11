package com.faforever.moderatorclient.api.dto;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Type("player")
public class Player {
    @Id
    private String id;
    private String login;
    @Relationship("names")
    List<NameRecord> names;
    private String email;
    private String userAgent;
    private String steamId;

    @Relationship("globalRating")
    private GlobalRating globalRating;

    @Relationship("ladder1v1Rating")
    private Ladder1v1Rating ladder1v1Rating;

    @Relationship("lobbyGroup")
    private LobbyGroup lobbyGroup;
}
