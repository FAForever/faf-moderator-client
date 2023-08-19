package com.faforever.moderatorclient.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
public class EnvironmentProperties {

    @NotBlank
    private String baseUrl;
    @NotBlank
    private String clientId;
    @NotBlank
    private String replayDownloadUrlFormat;
    @NotBlank
    private String oauthBaseUrl;
    @NotBlank
    private String oauthRedirectUrl;
    @NotBlank
    private String oauthScopes;
    @NotBlank
    private String userBaseUrl;

    private int maxPageSize = 10_000;
    private int maxResultSize = 1_000_000;
}
