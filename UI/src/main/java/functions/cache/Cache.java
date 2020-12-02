package functions.cache;

import java.util.HashMap;
import java.util.Map;

public class Cache {
    private static Map<String, UIContent> cache = new HashMap<>();

    public static UIContent getUIContent(String url) {
        return cache.get(url);
    }

    public static boolean isUIContentCached(String url) {
        return cache.containsKey(url);
    }

    public static void addUIContent(String url, String imageURL, String aggregatedFrequencies) {
        cache.put(url, new UIContent(imageURL, aggregatedFrequencies));
    }
}
