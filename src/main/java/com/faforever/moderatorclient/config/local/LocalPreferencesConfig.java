package com.faforever.moderatorclient.config.local;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class LocalPreferencesConfig {

    @Bean
    LocalPreferences localPreferences(LocalPreferencesReaderWriter reader) throws IOException {
        return reader.read();
    }
}
