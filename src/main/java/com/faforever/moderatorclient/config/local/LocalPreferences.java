package com.faforever.moderatorclient.config.local;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class LocalPreferences {
    private int version;
    private AutoLogin autoLogin = new AutoLogin();
    private UI ui = new UI();

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class AutoLogin {
        boolean enabled;
        String environment;
        String refreshToken;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class UI {
        boolean darkMode;
        String startUpTab;
    }
}
