package me.virjid.karting.json.util;

import me.virjid.karting.json.annotation.TimeFormat;
import me.virjid.karting.json.annotation.Transient;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Virjid
 */
public class ReflectUtil {
    // 是否为数组或列表
    public static boolean isList(Object obj) {
        if (obj == null) return false;

        Class<?> type = obj.getClass();

        return obj instanceof List || type.isArray();
    }

    // 是否为一个Map
    public static boolean isMap(Object obj) {
        if (obj == null) return false;

        return obj instanceof Map;
    }

    public static boolean isDateTime(Object obj) {
        return obj instanceof TemporalAccessor;
    }

    // 是否为包装类型
    public static boolean isWrapper(Class<?> type) {
        return type == Integer.class || type == Long.class || type == Boolean.class
                || type == Double.class || type == Float.class
                || type == Character.class || type == Byte.class
                || type == Short.class;
    }

    // 是否没有可能成为一个JSON对象
    public static boolean isNotJSONObject(Object val) {
        if (val == null) return true;

        Class<?> type = val.getClass();
        return type.isPrimitive() || isWrapper(type) || type == String.class;
    }

    // 是否可以成为一个JSON对象
    public static boolean isJSONObject(Object val) {
        return !isNotJSONObject(val);
    }

    // 将一个Object对象转为Map
    @NotNull
    public static Map<String, Object> objectToMap(@NotNull Object obj) {
        Map<String, Object> map = new HashMap<>();

        Field[] fields = obj.getClass().getDeclaredFields();

        try {
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod) || Modifier.isTransient(mod)) {
                    continue;
                }

                // 获取Transient注解
                if (field.getAnnotation(Transient.class) != null) {
                    continue;
                }

                boolean accessible = field.isAccessible();
                field.setAccessible(true);

                String key = field.getName();
                Object val = field.get(obj);

                // 处理时间类型
                if (isDateTime(val)) {
                    TimeFormat timeFormat = field.getAnnotation(TimeFormat.class);
                    if (timeFormat == null) {
                        map.put(key, val);
                    } else {
                        String pattern  = timeFormat.value();

                        if ("".equals(pattern)) {
                            map.put(key, StringUtil.dateTimeToString((TemporalAccessor) val));
                        } else {
                            map.put(key, StringUtil.dateTimeToString((TemporalAccessor) val, pattern));
                        }
                    }
                }
                // 直接存放至Map
                else {
                    map.put(key, val);
                }

                field.setAccessible(accessible);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return map;
    }
}
