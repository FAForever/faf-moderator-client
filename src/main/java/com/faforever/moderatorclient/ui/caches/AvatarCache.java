package com.faforever.moderatorclient.ui.caches;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class AvatarCache {

    private final Map<String, Image> cache;

    private static AvatarCache singleton;

    private AvatarCache() {
        this.cache = new HashMap<>();
    }

    public static synchronized AvatarCache getInstance() {
        if (singleton == null) {
            singleton = new AvatarCache();
        }
        return singleton;
    }

    public Image get(String key) {
        return this.cache.get(key);
    }

    public boolean containsKey(String key) {
        return this.cache.containsKey(key);
    }

    public void put(String cacheKey, Image img) {
        this.cache.put(cacheKey, img);
    }
}
