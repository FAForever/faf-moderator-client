package com.faforever.moderatorclient.config.local;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalPreferencesReaderWriter {
    private static final int CURRENT_VERSION = 1;
    private final Path prefsPath = Paths.get("client-prefs.json");

    private final ObjectMapper objectMapper;

    public LocalPreferencesAccessor read() throws IOException {
        if (Files.notExists(prefsPath)) {
            log.info("No preferences found at {}", prefsPath);
            return new LocalPreferencesAccessor(objectMapper, newConfig());
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(Files.newBufferedReader(prefsPath));

            ObjectNode node = switch (jsonNode) {
                case ObjectNode n -> migrate(n);
                default -> {
                    log.error("Could not load client preferences (client-prefs.json seems to be no valid JSON object");
                    yield newConfig();
                }
            };

            return new LocalPreferencesAccessor(objectMapper, node);
        } catch (IOException e) {
            log.error("Could not load client preferences", e);
            return new LocalPreferencesAccessor(objectMapper, newConfig());
        }
    }

    private ObjectNode newConfig() {
        ObjectNode emptyNode = objectMapper.createObjectNode();
        return migrate(emptyNode);
    }

    public void write(ObjectNode node) throws IOException {
        Files.writeString(prefsPath, objectMapper.writeValueAsString(node));
    }

    private ObjectNode migrate(ObjectNode node) {
        int version = Optional.ofNullable(node.get("version"))
                .map(JsonNode::asInt)
                .orElse(0);

        switch (version) {
            case 0:
                log.info("Creating new preferences");
                break;
            // add your case by case migrations here WITHOUT a break statement
            case CURRENT_VERSION:
                log.info("Preferences is up to date");
                break;
            default:
                log.warn("Invalid preferences version {}", version);
        }

        node.put("version", CURRENT_VERSION);

        return node;
    }
}
