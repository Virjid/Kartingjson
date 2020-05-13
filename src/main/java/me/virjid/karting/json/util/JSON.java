package me.virjid.karting.json.util;

import me.virjid.karting.json.exception.JSONTypeException;
import me.virjid.karting.json.model.JSONArray;
import me.virjid.karting.json.model.JSONObject;
import me.virjid.karting.json.parser.CharReader;
import me.virjid.karting.json.parser.JSONParser;
import me.virjid.karting.json.parser.TokenList;
import me.virjid.karting.json.parser.Tokenizer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringReader;
import java.time.temporal.TemporalAccessor;
import java.util.Map;
import java.util.Set;

/**
 * @author Virjid
 */
public class JSON {

    private static final char SPACE_CHAR = ' ';

    private static final JSONParser parser   = new JSONParser();
    private static final Tokenizer tokenizer = new Tokenizer();

    // ----------------------------------------------
    // json object or json array to string
    // ----------------------------------------------

    @NotNull
    @Contract(pure = true)
    public static String toJSONString(JSONObject model, int indent) {
        return toJSONString(model, 0, indent);
    }

    @NotNull
    @Contract(pure = true)
    public static String toJSONString(JSONObject model) {
        return toJSONString(model, 0, 0);
    }

    @NotNull
    public static String toJSONString(JSONArray array, int indent) {
        return toJSONString(array, 0, indent);
    }

    @NotNull
    public static String toJSONString(JSONArray array) {
        return toJSONString(array, 0, 0);
    }

    @NotNull
    @Contract(pure = true)
    private static String toJSONString(@NotNull JSONObject model, int depth, final int indent) {
        // 以左大括号作为起始
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        depth++;

        // 计算缩进
        String indentStr = calcIndent(depth, indent);

        Set<Map.Entry<String, Object>> entries = model.entrySet();
        final int size = entries.size();
        int i = 0;
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();

            // <indent>"<key>":<space>
            if (indent > 0) sb.append('\n');
            sb.append(indentStr).append('"').append(key).append("\":");
            if (indent > 0) sb.append(' ');

            if (val instanceof JSONObject) {
                sb.append(toJSONString((JSONObject) val, depth, indent));
            } else if (val instanceof JSONArray) {
                sb.append(toJSONString((JSONArray) val, depth, indent));
            } else if (val instanceof String) {
                String handleStr = handleString((String) val);
                sb.append('"').append(handleStr).append('"');
            } else {
                sb.append(val);
            }

            if (i < size - 1) sb.append(',');
            else if (indent > 0) sb.append('\n');

            i++;
        }

        // 结束解析
        depth--;
        sb.append(calcIndent(depth, indent)).append('}');

        return sb.toString();
    }

    @NotNull
    private static String toJSONString(@NotNull JSONArray array, int depth, final int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        depth++;
        String indentStr = calcIndent(depth, indent);

        for (int i = 0, size = array.size(); i < size; i++) {

            if (indent > 0) sb.append('\n');

            Object item = array.get(i);
            if (item instanceof JSONObject) {
                sb.append(toJSONString((JSONObject) item, depth, indent));
            } else if (item instanceof JSONArray) {
                sb.append(toJSONString((JSONArray) item, depth, indent));
            } else if (item instanceof String) {
                String handleStr = handleString((String) item);
                sb.append(indentStr).append('"').append(handleStr).append('"');
            } else {
                sb.append(indentStr).append(item);
            }

            if (i < size - 1) sb.append(',');
            else if (indent > 0) sb.append('\n');
        }

        depth--;
        sb.append(calcIndent(depth, indent)).append(']');

        return sb.toString();
    }

    // 字符串格式化时，有些字符需要被转义
    @NotNull
    private static String handleString(@NotNull String item) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, size = item.length(); i < size; i++) {
            char c = item.charAt(i);

            if (c == '"') sb.append("\\\"");
            else if (c == '\\') sb.append("\\\\");
            else sb.append(c);
        }
        return sb.toString();
    }

    @NotNull
    private static String calcIndent(int depth, int indent) {
        if (indent <= 0) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0, size = depth * indent; i < size; i++) {
            sb.append(SPACE_CHAR);
        }
        return sb.toString();
    }

    // ----------------------------------------------
    // string to json object or json array
    // ----------------------------------------------
    @NotNull
    public static JSONObject parseJSONObject(String source) {
        try {
            CharReader reader   = new CharReader(new StringReader(source));
            TokenList tokenList = tokenizer.tokenize(reader);
            return parser.parseJSONObject(tokenList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static JSONArray parseJSONArray(String source) {
        try {
            CharReader reader   = new CharReader(new StringReader(source));
            TokenList tokenList = tokenizer.tokenize(reader);
            return parser.parseJSONArray(tokenList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // ----------------------------------------------
    // any object to json object or json array
    // ----------------------------------------------
    public static JSONObject toJSONObject(Object obj) {
        if (obj == null) return null;

        if (ReflectUtil.isList(obj)) {
            throw new JSONTypeException("Type of value is not JSONObject");
        }

        if (obj instanceof JSONObject) {
            return (JSONObject) obj;
        }

        JSONObject model = new JSONObject();
        toJSONObject(obj, model);
        return model;
    }

    public static JSONArray toJSONArray(Object obj) {
        if (obj == null) return null;

        if (!ReflectUtil.isList(obj)) {
            throw new JSONTypeException("Type of value is not JSONArray");
        }

        if (obj instanceof JSONArray) {
            return (JSONArray) obj;
        }

        JSONArray array = new JSONArray();
        toJSONArray(obj, array);
        return array;
    }

    private static void toJSONObject(Object obj, JSONObject model) {
        if (!ReflectUtil.isMap(obj)) {
            toJSONObject(ReflectUtil.objectToMap(obj), model);
        } else {
            toJSONObject((Map<?, ?>) obj, model);
        }
    }

    private static void toJSONObject(@NotNull Map<?, ?> map, JSONObject model) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            Object val = entry.getValue();

            // 处理JSON的基本类型
            if (ReflectUtil.isNotJSONObject(val)) {
                model.put(key, val);
            }
            // 处理JSON列表
            else if (ReflectUtil.isList(val)) {
                JSONArray array = new JSONArray();
                toJSONArray(val, array);
                model.put(key, array);
            }
            // 如果又是一个Map
            else if (ReflectUtil.isMap(val)) {
                JSONObject subModel = new JSONObject();
                toJSONObject((Map<?, ?>) val, subModel);
                model.put(key, subModel);
            }
            // 处理时间对象：LocalDateTime、LocalDate、LocalTime
            else if (ReflectUtil.isDateTime(val)) {
                model.put(key, StringUtil.dateTimeToString((TemporalAccessor) val));
            }
            // 处理其他对象
            else {
                JSONObject subModel = new JSONObject();
                toJSONObject(ReflectUtil.objectToMap(val), subModel);
                model.put(key, subModel);
            }
        }
    }

    private static void toJSONArray(@NotNull Object obj, JSONArray array) {
        for (Object item : ReflectUtil.asList(obj)) {
            if (ReflectUtil.isNotJSONObject(item)) {
                array.add(item);
            }

            else if (ReflectUtil.isList(item)) {
                JSONArray subArray = new JSONArray();
                toJSONArray(item, subArray);
                array.add(subArray);
            }

            // 处理时间对象：LocalDateTime、LocalDate、LocalTime
            else if (ReflectUtil.isDateTime(item)) {
                array.add(StringUtil.dateTimeToString((TemporalAccessor) item));
            }

            else {
                JSONObject model = new JSONObject();
                toJSONObject(item, model);
                array.add(model);
            }
        }
    }
}
