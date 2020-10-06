package com.faforever.moderatorclient.ui.caches;

import javafx.scene.image.Image;

public abstract class ThumbnailCache {

    public abstract Image get(String key);
    public abstract boolean containsKey(String key);
    public abstract void put(String cacheKey, Image img);

    public Image fromIdAndString(String id, String url) {
        if (id == null || url == null) return null;
        if (containsKey(id)) return get(id);
        String sanitisedURL = url.replace(" ", "%20");
        Image img = new Image(sanitisedURL, true);
        put(id, img);
        return img;
    }
}
