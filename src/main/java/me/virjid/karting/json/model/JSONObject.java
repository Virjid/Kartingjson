package me.virjid.karting.json.model;

import me.virjid.karting.json.annotation.TimeFormat;
import me.virjid.karting.json.exception.JSONTypeException;
import me.virjid.karting.json.util.JSON;
import me.virjid.karting.json.util.ReflectUtil;
import me.virjid.karting.json.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
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

    public Object get(String key) {
        return map.get(key);
    }

    public String getString(String key) {
        Object obj = map.get(key);
        if (!(obj instanceof String)) {
            throw new JSONTypeException("Type of value is not String");
        }

        return (String) obj;
    }

    public Long getLong(String key) {
        Object obj = map.get(key);
        if (!(obj instanceof Long)) {
            throw new JSONTypeException("Type of value is not Long");
        }

        return (Long) obj;
    }

    public Integer getInteger(String key) {
        Object obj = map.get(key);
        if (!(obj instanceof Integer)) {
            throw new JSONTypeException("Type of value is not Integer");
        }

        return (Integer) obj;
    }

    public Short getShort(String key) {
        Object obj = map.get(key);
        if (!(obj instanceof Short)) {
            throw new JSONTypeException("Type of value is not Short");
        }

        return (Short) obj;
    }

    public Byte getByte(String key) {
        Object obj = map.get(key);
        if (!(obj instanceof Byte)) {
            throw new JSONTypeException("Type of value is not Byte");
        }

        return (Byte) obj;
    }

    public Character getCharacter(String key) {
        Object obj = map.get(key);
        if (!(obj instanceof Character)) {
            throw new JSONTypeException("Type of value is not Character");
        }

        return (Character) obj;
    }

    public Double getDouble(String key) {
        Object obj = map.get(key);
        if (!(obj instanceof Double)) {
            throw new JSONTypeException("Type of value is not Double");
        }

        return (Double) obj;
    }

    public Float getFloat(String key) {
        Object obj = map.get(key);
        if (!(obj instanceof Float)) {
            throw new JSONTypeException("Type of value is not Float");
        }

        return (Float) obj;
    }

    public Boolean getBoolean(String key) {
        Object obj = map.get(key);
        if (!(obj instanceof Boolean)) {
            throw new JSONTypeException("Type of value is not Boolean");
        }

        return (Boolean) obj;
    }

    public JSONObject getJSONObject(String key) {
        Object obj = map.get(key);
        if (!(obj instanceof JSONObject)) {
            throw new JSONTypeException("Type of value is not JSONObject");
        }

        return (JSONObject) obj;
    }

    public JSONArray getJSONArray(String key) {
        Object obj = map.get(key);
        if (!(obj instanceof JSONArray)) {
            throw new JSONTypeException("Type of value is not JSONArray");
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

    public void toObject(@NotNull Object o) throws Exception {
        Class<?> type = o.getClass();
        Field[] fields = type.getDeclaredFields();

        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isFinal(mod)) continue;

            field.setAccessible(true);
            String name = field.getName();
            Object val  = get(name);
            Class<?> fieldType = field.getType();

            if (ReflectUtil.isNotJSONObject(fieldType)) {
                field.set(o, val);
            }
            else if (ReflectUtil.isList(fieldType)) {
                JSONArray array = (JSONArray) val;
                List<Object> list = new ArrayList<>(array.size());

                if (fieldType.isArray()) {
                    array.toList(list, fieldType.getComponentType());
                    Object[] a = (Object[]) Array.newInstance(fieldType.getComponentType(), array.size());
                    for (int i = 0, size = array.size(); i < size; i++) {
                        a[i] = list.get(i);
                    }
                    field.set(o, a);
                } else {
                    array.toList(list, (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
                    field.set(o, list);
                }
            }
            else if (ReflectUtil.isDateTime(fieldType)) {
                TimeFormat format = field.getAnnotation(TimeFormat.class);
                if (format == null) {
                    field.set(o, StringUtil.stringToDateTime((String) val, fieldType));
                } else {
                    String pattern = format.value();
                    if ("".equals(pattern)) {
                        field.set(o, StringUtil.stringToDateTime((String) val, fieldType));
                    } else {
                        field.set(o, StringUtil.stringToDateTime((String) val, fieldType, pattern));
                    }
                }
            }
            else {
                Object subObj = fieldType.newInstance();
                ((JSONObject) val).toObject(subObj);
            }
        }
    }
}
