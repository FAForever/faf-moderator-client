package com.faforever.moderatorclient.config;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@Data
public class EnvironmentProperties {

    @NotBlank
    private String baseUrl;

    @NotBlank
    private String clientId;

    @NotBlank
    private String clientSecret;

    @NotBlank
    private String accessTokenUri;

    @NotBlank
    private String replayDownloadUrlFormat;

    private int maxPageSize = 10_000;
    private int maxResultSize = 1_000_000;
}
