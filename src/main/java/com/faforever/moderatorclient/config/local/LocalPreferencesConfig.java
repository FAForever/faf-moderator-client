package com.faforever.moderatorclient.config.local;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class LocalPreferencesConfig {

    @Bean
    LocalPreferencesAccessor localPreferencesAccessor(ObjectMapper objectMapper) throws IOException {
        var localPreferencesReaderWriter = new LocalPreferencesReaderWriter(objectMapper);

        return localPreferencesReaderWriter.read();
    }
}
