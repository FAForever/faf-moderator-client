package com.faforever.moderatorclient.api.dto;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @deprecated LobbyGroups are supposed to be replaced with role based security
 */
@Getter
@Deprecated
@NoArgsConstructor
@AllArgsConstructor
@Type("lobbyGroup")
public class LobbyGroup {
    @Id
    private String userId;
    private LegacyAccessLevel accessLevel;
}
