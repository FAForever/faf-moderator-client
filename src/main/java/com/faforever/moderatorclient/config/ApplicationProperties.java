package com.faforever.moderatorclient.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@ConfigurationProperties(prefix = "faforever")
@Data
@Validated
public class ApplicationProperties {
    @NotEmpty
    @Valid
    private Map<String, EnvironmentProperties> environments;
}
