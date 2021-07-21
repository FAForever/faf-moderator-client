package com.faforever.moderatorclient.api.dto.hydra;

import lombok.Value;

@Value
public class RevokeRefreshTokenRequest {
    String subject;
    String client;
    boolean all;

    public static RevokeRefreshTokenRequest allClientsOf(String playerId) {
        return new RevokeRefreshTokenRequest(playerId, null, true);
    }
}
