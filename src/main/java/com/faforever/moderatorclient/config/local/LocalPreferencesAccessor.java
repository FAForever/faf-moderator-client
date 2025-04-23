package com.faforever.moderatorclient.config.local;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class LocalPreferencesAccessor {
    private static final String ENABLED_KEY = "enabled";
    private static final String AUTO_LOGIN_KEY = "autoLogin";
    private static final String ENVIRONMENT_KEY = "environment";
    private static final String REFRESH_TOKEN_KEY = "refreshToken";

    private final ObjectMapper objectMapper;
    @Getter
    private final ObjectNode node;

    public boolean isAutoLoginEnabled() {
        return Optional.ofNullable(node.get(AUTO_LOGIN_KEY))
                .map(node -> node.get(ENABLED_KEY))
                .map(JsonNode::asBoolean)
                .orElse(false);
    }

    public Optional<String> getRefreshToken() {
        return Optional.ofNullable(node.get(AUTO_LOGIN_KEY))
                .map(node -> node.get(REFRESH_TOKEN_KEY))
                .map(JsonNode::asText);
    }

    public Optional<String> getEnvironment() {
        return Optional.ofNullable(node.get(AUTO_LOGIN_KEY))
                .map(node -> node.get(ENVIRONMENT_KEY))
                .map(JsonNode::asText);
    }

    public void setAutoLoginEnabled(boolean enabled) {
        putValueToPath(List.of(AUTO_LOGIN_KEY, ENABLED_KEY), enabled);
    }

    public void setRefreshToken(String refreshToken) {
        putValueToPath(List.of(AUTO_LOGIN_KEY, REFRESH_TOKEN_KEY), refreshToken);
    }

    public void setEnvironment(String environment) {
        putValueToPath(List.of(AUTO_LOGIN_KEY, ENVIRONMENT_KEY), environment);
    }

    private void putValueToPath(Collection<String> path, boolean value) {
        List<String> mutablePath = new ArrayList<>(path);

        ObjectNode currentNode = this.node;
        while (mutablePath.size() > 1) {
            String next = mutablePath.getFirst();
            if (node.has(next)) {
                currentNode = (ObjectNode) node.get(next);
            } else {
                currentNode = currentNode.putObject(next);
            }

            mutablePath.removeFirst();
        }

        currentNode.put(mutablePath.getFirst(), value);
    }

    private void putValueToPath(Collection<String> path, String value) {
        List<String> mutablePath = new ArrayList<>(path);

        ObjectNode currentNode = this.node;
        while (mutablePath.size() > 1) {
            String next = mutablePath.getFirst();
            if (node.has(next)) {
                currentNode = (ObjectNode) node.get(next);
            } else {
                currentNode = currentNode.putObject(next);
            }

            mutablePath.removeFirst();
        }

        currentNode.put(mutablePath.getFirst(), value);
    }
}
