package com.faforever.moderatorclient.ui.caches;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class SmallThumbnailCache extends ThumbnailCache {

    private final Map<String, Image> smallThumbnailCache;

    private static SmallThumbnailCache singleton;

    private SmallThumbnailCache() {
        this.smallThumbnailCache = new HashMap<>();
    }

    public static synchronized SmallThumbnailCache getInstance() {
        if (singleton == null) {
            singleton = new SmallThumbnailCache();
        }
        return singleton;
    }

    @Override
    public Image get(String key) {
        return this.smallThumbnailCache.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return this.smallThumbnailCache.containsKey(key);
    }

    @Override
    public void put(String cacheKey, Image img) {
        this.smallThumbnailCache.put(cacheKey, img);
    }

}
