package com.faforever.moderatorclient.config.local;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class LocalPreferences {
    private int version;
    private AutoLogin autoLogin = new AutoLogin();

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class AutoLogin {
        Boolean enabled;
        String environment;
        String refreshToken;
    }
}
