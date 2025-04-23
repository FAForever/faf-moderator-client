package com.faforever.moderatorclient.config.local;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class LocalPreferencesAccessorTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getEmptyValues() {
        LocalPreferencesAccessor sut = new LocalPreferencesAccessor(
                objectMapper,
                objectMapper.createObjectNode()
        );

        assertThat(sut.isAutoLoginEnabled(), equalTo(false));
        assertThat(sut.getRefreshToken(), equalTo(Optional.empty()));
        assertThat(sut.getEnvironment(), equalTo(Optional.empty()));
    }

    @Test
    void setAndGetAutoLoginEnabled() {
        LocalPreferencesAccessor sut = new LocalPreferencesAccessor(
                objectMapper,
                objectMapper.createObjectNode()
        );

        sut.setAutoLoginEnabled(true);
        var result = sut.isAutoLoginEnabled();

        assertThat(result, equalTo(true));
    }

    @Test
    void setAndGetRefreshToken() {
        LocalPreferencesAccessor sut = new LocalPreferencesAccessor(
                objectMapper,
                objectMapper.createObjectNode()
        );

        sut.setRefreshToken("test");
        var result = sut.getRefreshToken().get();

        assertThat(result, equalTo("test"));
    }

    @Test
    void setAndGetEnvironment() {
        LocalPreferencesAccessor sut = new LocalPreferencesAccessor(
                objectMapper,
                objectMapper.createObjectNode()
        );

        sut.setEnvironment("test");
        var result = sut.getEnvironment().get();

        assertThat(result, equalTo("test"));
    }
}