package com.faforever.moderatorclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Map;

@ConfigurationProperties(prefix = "faforever")
@Data
@Validated
public class ApplicationProperties {
    @NotEmpty
    @Valid
    private Map<String, EnvironmentProperties> environments;
}
