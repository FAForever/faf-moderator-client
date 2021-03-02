package com.faforever.moderatorclient.ui.caches;

import javafx.scene.image.Image;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SmallThumbnailCache extends ThumbnailCache {

    private final Map<String, Image> smallThumbnailCache;

    private SmallThumbnailCache() {
        this.smallThumbnailCache = new HashMap<>();
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
