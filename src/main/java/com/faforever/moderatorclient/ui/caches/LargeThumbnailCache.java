package com.faforever.moderatorclient.ui.caches;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class LargeThumbnailCache extends ThumbnailCache {

    private final Map<String, Image> largeThumbnailCache;

    private static LargeThumbnailCache singleton;

    private LargeThumbnailCache() {
        this.largeThumbnailCache = new HashMap<>();
    }

    public static synchronized LargeThumbnailCache getInstance() {
        if (singleton == null) {
            singleton = new LargeThumbnailCache();
        }
        return singleton;
    }

    @Override
    public Image get(String key) {
        return this.largeThumbnailCache.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return this.largeThumbnailCache.containsKey(key);
    }

    @Override
    public void put(String cacheKey, Image img) {
        this.largeThumbnailCache.put(cacheKey, img);
    }
}
