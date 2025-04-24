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

    public LocalPreferences read() throws IOException {
        ObjectNode loadedNode = newConfig();

        if (Files.notExists(prefsPath)) {
            log.info("No preferences found at {}", prefsPath);
        } else {
            try {
                JsonNode jsonNode = objectMapper.readTree(Files.newBufferedReader(prefsPath));

                if (jsonNode instanceof ObjectNode n) {
                    loadedNode = migrate(n);
                } else {
                    log.error("Could not load client preferences (client-prefs.json seems to be no valid JSON object");
                }
            } catch (IOException e) {
                log.error("Could not load client preferences", e);
            }
        }

        return objectMapper.readValue(objectMapper.writeValueAsString(loadedNode), LocalPreferences.class);
    }

    private ObjectNode newConfig() {
        ObjectNode emptyNode = objectMapper.createObjectNode();
        return migrate(emptyNode);
    }

    public void write(LocalPreferences localPreferences) throws IOException {
        Files.writeString(prefsPath, objectMapper.writeValueAsString(localPreferences));
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
