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
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Map;

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
        StringBuilder sb = new StringBuilder();
        sb.append(getIndentString(depth, indent));
        sb.append("{");
        depth++;

        List<Map.Entry<String, Object>> keyValues = model.getAllKeyValue();
        int size = keyValues.size();
        for (int i = 0; i < size; i++) {
            Map.Entry<String, Object> keyValue = keyValues.get(i);

            String key = keyValue.getKey();
            Object value = keyValue.getValue();

            if (indent != 0) {
                sb.append("\n").append(getIndentString(depth, indent))
                        .append("\"").append(key).append("\"").append(": ");
                if (value instanceof JSONObject) {
                    sb.append("\n");
                    sb.append(toJSONString((JSONObject) value, depth, indent));
                } else if (value instanceof JSONArray){
                    sb.append("\n");
                    sb.append(toJSONString((JSONArray) value, depth, indent));
                } else if (value instanceof String) {
                    sb.append("\"");
                    sb.append(value);
                    sb.append("\"");
                } else {
                    sb.append(value);
                }
            } else {
                sb.append("\"").append(key).append("\":");
                if (value instanceof JSONObject) {
                    sb.append(toJSONString((JSONObject) value, depth, indent));
                } else if (value instanceof JSONArray){
                    sb.append(toJSONString((JSONArray) value, depth, indent));
                } else if (value instanceof String) {
                    sb.append("\"");
                    sb.append(value);
                    sb.append("\"");
                } else {
                    sb.append(value);
                }
            }
            if (i < size - 1) {
                sb.append(",");
            }
        }

        depth--;
        if (indent != 0) {
            sb.append("\n");
            sb.append(getIndentString(depth, indent));
        }
        sb.append("}");

        return sb.toString();
    }

    @NotNull
    private static String toJSONString(@NotNull JSONArray array, int depth, final int indent) {
        StringBuilder sb = new StringBuilder();
        if (indent != 0) {
            sb.append(getIndentString(depth, indent));
        }
        sb.append("[");
        depth++;

        int size = array.size();
        for (int i = 0; i < size; i++) {
            if (indent != 0) {
                sb.append("\n");
            }

            Object ele = array.get(i);
            if (ele instanceof JSONObject) {
                sb.append(toJSONString((JSONObject) ele, depth, indent));
            } else if (ele instanceof JSONArray) {
                sb.append(toJSONString((JSONArray) ele, depth, indent));
            } else if (ele instanceof String) {
                sb.append(getIndentString(depth, indent));
                sb.append("\"");
                sb.append(ele);
                sb.append("\"");
            } else {
                sb.append(getIndentString(depth, indent));
                sb.append(ele);
            }

            if (i < size - 1) {
                sb.append(",");
            }
        }

        depth--;
        if (indent != 0) {
            sb.append("\n");
            sb.append(getIndentString(depth, indent));
        }
        sb.append("]");

        return sb.toString();
    }

    @NotNull
    private static String getIndentString(int depth, int indent) {
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

        JSONObject model = new JSONObject();
        toJSONObject(obj, model);

        return model;
    }

    public static JSONArray toJSONArray(Object obj) {
        if (obj == null) return null;

        if (!(obj instanceof List) && !obj.getClass().isArray()) {
            throw new JSONTypeException("Type of value is not JSONArray");
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

        if (obj.getClass().isArray()) {
            Class<?> componentType = obj.getClass().getComponentType();

            if (componentType == int.class) {
                int[] objs = (int[]) obj;
                for (int i : objs) {
                    array.add(i);
                }
            } else if (componentType == long.class) {
                long[] objs = (long[]) obj;
                for (long i : objs) {
                    array.add(i);
                }
            } else if (componentType == char.class) {
                char[] objs = (char[]) obj;
                for (char i : objs) {
                    array.add(i);
                }
            } else if (componentType == short.class) {
                short[] objs = (short[]) obj;
                for (short i : objs) {
                    array.add(i);
                }
            } else if (componentType == double.class) {
                double[] objs = (double[]) obj;
                for (double i : objs) {
                    array.add(i);
                }
            } else if (componentType == float.class) {
                float[] objs = (float[]) obj;
                for (float i : objs) {
                    array.add(i);
                }
            } else if (componentType == byte.class) {
                byte[] objs = (byte[]) obj;
                for (byte i : objs) {
                    array.add(i);
                }
            } else if (componentType == boolean.class) {
                boolean[] objs = (boolean[]) obj;
                for (boolean i : objs) {
                    array.add(i);
                }
            } else {
                Object[] objs = (Object[]) obj;

                for (Object o : objs) {
                    if (o == null) {
                        array.add(null);
                        continue;
                    }

                    JSONObject model = new JSONObject();
                    toJSONObject(o, model);
                    array.add(model);
                }
            }
        } else {
            List<?> list = (List<?>) obj;

            for (Object item : list) {
                if (item == null) {
                    array.add(null);
                    continue;
                }

                JSONObject model = new JSONObject();
                toJSONObject(item, model);
                array.add(model);
            }
        }
    }
}
