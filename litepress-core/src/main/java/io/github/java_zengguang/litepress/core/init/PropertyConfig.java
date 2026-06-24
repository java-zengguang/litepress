package io.github.java_zengguang.litepress.core.init;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyConfig {
    private static final Trie<String, String> trie = new PatriciaTrie<>();

    private static PropertyConfig propertyConfig;

    private PropertyConfig() {
    }

    public static PropertyConfig getInstance() {
        if (propertyConfig == null) {
            propertyConfig = new PropertyConfig();
        }
        return propertyConfig;
    }

    public void load(String doc) throws IOException {
        Properties properties = new Properties();
        properties.load(new StringReader(doc));
        properties.forEach((key, value) -> {
            trie.put("" + key, "" + value);
        });
    }

    public Map<String, String> getSubMap(String prefix) {
        Map<String, String> resultMap = new HashMap<>();
        Map<String, String> subMap = trie.prefixMap(prefix);
        subMap.forEach((key, value) -> {
            if (key.startsWith(prefix)) {
                String newKey = key.substring(prefix.length());
                resultMap.put(newKey, value);
            }
        });
        return resultMap;
    }

    public Object getValue(String key) {
        return trie.get(key);
    }


}
