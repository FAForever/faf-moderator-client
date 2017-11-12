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
    @RestrictedVisibility("IsModerator")
    private String email;
    private String userAgent;
    @RestrictedVisibility("IsModerator")
    private String steamId;

    @Relationship("globalRating")
    private GlobalRating globalRating;

    @Relationship("ladder1v1Rating")
    private Ladder1v1Rating ladder1v1Rating;

    @Relationship("lobbyGroup")
    private LobbyGroup lobbyGroup;

    @Relationship("bans")
    private List<BanInfo> bans;

    @Override
    public String toString() {
        return String.format("%s [id %s]", login, id);
    }
}
