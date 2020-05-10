package me.virjid.karting.json.model;

import me.virjid.karting.json.exception.JSONTypeException;
import me.virjid.karting.json.util.JSON;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

/**
 * @author Virjid
 */
public class JSONObject implements Map<String, Object>, Serializable {
    private static final long serialVersionUID = -8645173229129717605L;

    private Map<String, Object> map = new HashMap<>();

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return map.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @NotNull
    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @NotNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    public List<Entry<String, Object>> getAllKeyValue() {
        return new ArrayList<>(map.entrySet());
    }

    public JSONObject getJSONObject(String key) {
        if (!map.containsKey(key)) {
            throw new IllegalArgumentException("Invalid key");
        }

        Object obj = map.get(key);
        if (!(obj instanceof JSONObject)) {
            throw new JSONTypeException("Type of value is not JsonObject");
        }

        return (JSONObject) obj;
    }

    public JSONArray getJSONArray(String key) {
        if (!map.containsKey(key)) {
            throw new IllegalArgumentException("Invalid key");
        }

        Object obj = map.get(key);
        if (!(obj instanceof JSONArray)) {
            throw new JSONTypeException("Type of value is not JsonArray");
        }

        return (JSONArray) obj;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public String toString(int indent) {
        return JSON.toJSONString(this, indent);
    }
}
