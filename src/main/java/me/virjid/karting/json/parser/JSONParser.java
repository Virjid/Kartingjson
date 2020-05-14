package me.virjid.karting.json.parser;

import me.virjid.karting.json.exception.JSONParseException;
import me.virjid.karting.json.model.JSONArray;
import me.virjid.karting.json.model.JSONObject;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import static me.virjid.karting.json.parser.TokenType.*;

/**
 * @author Virjid
 */
public class JSONParser {

    public <T> T parseObject(@NotNull TokenList tokens, Class<T> type) throws Exception {
        if (tokens.next().type() != BEGIN_OBJECT) {
            throw new JSONParseException("Parse error, invalid Token.");
        }

        T obj = type.newInstance();

        int expectToken = TokenType.calcCode(STRING, END_OBJECT);

        String key  = null;
        Field field = null;

        while (tokens.hasNext()) {
            Token token = tokens.next();
            TokenType tokenType = token.type();
            String tokenValue = token.value();
            if (key != null) {
                field = type.getDeclaredField(key);
                field.setAccessible(true);
            }
            switch (tokenType) {
                case BEGIN_OBJECT:
                    checkExpectToken(tokenType, expectToken);
                    tokens.back();
                    field.set(obj, parseObject(tokens, field.getType()));
                    expectToken = TokenType.calcCode(SEP_COMMA, END_OBJECT);
                    break;
                case END_OBJECT:
                case END_DOCUMENT:
                    checkExpectToken(tokenType, expectToken);
                    return obj;
                case BEGIN_ARRAY:
                    checkExpectToken(tokenType, expectToken);
                    tokens.back();

                    if (field.getType().isArray()) {
                        field.set(obj, parseArray(tokens, field.getType(),
                                field.getType().getComponentType()));
                    } else {
                        field.set(obj, parseArray(tokens, field.getType(),
                                (Class<?>) ((ParameterizedType) field.getGenericType())
                                        .getActualTypeArguments()[0]));
                    }

                    expectToken = TokenType.calcCode(SEP_COMMA, END_OBJECT);
                    break;
                case NULL:
                    checkExpectToken(tokenType, expectToken);
                    field.set(obj, null);
                    expectToken = TokenType.calcCode(SEP_COMMA, END_OBJECT);
                    break;
                case NUMBER:
                    checkExpectToken(tokenType, expectToken);
                    Class<?> fieldType = field.getType();
                    if (fieldType == Integer.class || fieldType == int.class) {
                        field.set(obj, Integer.valueOf(tokenValue));
                    } else if (fieldType == Double.class || fieldType == double.class) {
                        field.set(obj, Double.valueOf(tokenValue));
                    } else if (fieldType == Short.class || fieldType == short.class) {
                        field.set(obj, Short.valueOf(tokenValue));
                    } else if (fieldType == Long.class || fieldType == long.class) {
                        field.set(obj, Long.valueOf(tokenValue));
                    } else if (fieldType == Byte.class || fieldType == byte.class) {
                        field.set(obj, Byte.valueOf(tokenValue));
                    } else if (fieldType == Float.class || fieldType == float.class) {
                        field.set(obj, Float.valueOf(tokenValue));
                    }

                    expectToken =TokenType.calcCode(SEP_COMMA, END_OBJECT);
                    break;
                case BOOLEAN:
                    checkExpectToken(tokenType, expectToken);
                    field.set(obj, Boolean.valueOf(tokenValue));
                    expectToken = TokenType.calcCode(SEP_COMMA, END_OBJECT);
                    break;
                case STRING:
                    checkExpectToken(tokenType, expectToken);
                    Token preToken = tokens.peekPrevious();

                    if (preToken.type() == SEP_COLON) {
                        field.set(obj, tokenValue);
                        expectToken = TokenType.calcCode(SEP_COMMA, END_OBJECT);
                    } else {
                        key = token.value();
                        expectToken = TokenType.calcCode(SEP_COLON);
                    }
                    break;
                case SEP_COLON:
                    checkExpectToken(tokenType, expectToken);
                    expectToken = TokenType.calcCode(NULL, NUMBER, BOOLEAN,
                            STRING, BEGIN_OBJECT, BEGIN_ARRAY);
                    break;
                case SEP_COMMA:
                    checkExpectToken(tokenType, expectToken);
                    expectToken = TokenType.calcCode(STRING);
                    break;
                default:
                    throw new JSONParseException("Unexpected Token.");
            }
        }

        throw new JSONParseException("Parse error, invalid Token.");
    }

    public <T> List<T> parseArray(@NotNull TokenList tokens, Class<?> type, Class<T> componentType) throws Exception {
        throw new JSONParseException("Has not supported the type => " + type);
    }

    @NotNull
    public JSONObject parseJSONObject(@NotNull TokenList tokens) {
        if (tokens.next().type() != BEGIN_OBJECT) {
            throw new JSONParseException("Parse error, invalid Token.");
        }

        JSONObject jsonObject = new JSONObject();
        int expectToken = TokenType.calcCode(STRING, END_OBJECT);
        String key = null;
        Object value;
        while (tokens.hasNext()) {
            Token token = tokens.next();
            TokenType tokenType = token.type();
            String tokenValue = token.value();
            switch (tokenType) {
                case BEGIN_OBJECT:
                    checkExpectToken(tokenType, expectToken);
                    tokens.back();
                    jsonObject.put(key, parseJSONObject(tokens));
                    expectToken = TokenType.calcCode(SEP_COMMA, END_OBJECT);
                    break;
                case END_OBJECT:
                case END_DOCUMENT:
                    checkExpectToken(tokenType, expectToken);
                    return jsonObject;
                case BEGIN_ARRAY:
                    checkExpectToken(tokenType, expectToken);
                    tokens.back();
                    jsonObject.put(key, parseJSONArray(tokens));
                    expectToken = TokenType.calcCode(SEP_COMMA, END_OBJECT);
                    break;
                case NULL:
                    checkExpectToken(tokenType, expectToken);
                    jsonObject.put(key, null);
                    expectToken = TokenType.calcCode(SEP_COMMA, END_OBJECT);
                    break;
                case NUMBER:
                    checkExpectToken(tokenType, expectToken);
                    if (tokenValue.contains(".") || tokenValue.contains("e") || tokenValue.contains("E")) {
                        jsonObject.put(key, Double.valueOf(tokenValue));
                    } else {
                        long num = Long.parseLong(tokenValue);
                        if (num > Integer.MAX_VALUE || num < Integer.MIN_VALUE) {
                            jsonObject.put(key, num);
                        } else {
                            jsonObject.put(key, (int) num);
                        }
                    }
                    expectToken =TokenType.calcCode(SEP_COMMA, END_OBJECT);
                    break;
                case BOOLEAN:
                    checkExpectToken(tokenType, expectToken);
                    jsonObject.put(key, Boolean.valueOf(token.value()));
                    expectToken = TokenType.calcCode(SEP_COMMA, END_OBJECT);
                    break;
                case STRING:
                    checkExpectToken(tokenType, expectToken);
                    Token preToken = tokens.peekPrevious();

                    if (preToken.type() == SEP_COLON) {
                        value = token.value();
                        jsonObject.put(key, value);
                        expectToken = TokenType.calcCode(SEP_COMMA, END_OBJECT);
                    } else {
                        key = token.value();
                        expectToken = TokenType.calcCode(SEP_COLON);
                    }
                    break;
                case SEP_COLON:
                    checkExpectToken(tokenType, expectToken);
                    expectToken = TokenType.calcCode(NULL, NUMBER, BOOLEAN,
                            STRING, BEGIN_OBJECT, BEGIN_ARRAY);
                    break;
                case SEP_COMMA:
                    checkExpectToken(tokenType, expectToken);
                    expectToken = TokenType.calcCode(STRING);
                    break;
                default:
                    throw new JSONParseException("Unexpected Token.");
            }
        }

        throw new JSONParseException("Parse error, invalid Token.");
    }

    @NotNull
    public JSONArray parseJSONArray(@NotNull TokenList tokens) {
        if (tokens.next().type() != BEGIN_ARRAY) {
            throw new JSONParseException("Parse error, invalid Token.");
        }

        int expectToken = TokenType.calcCode(BEGIN_ARRAY, END_ARRAY, BEGIN_OBJECT,
                NULL, NUMBER, BOOLEAN, STRING);
        JSONArray array = new JSONArray();
        while (tokens.hasNext()) {
            Token token = tokens.next();
            TokenType tokenType = token.type();
            String tokenValue   = token.value();
            switch (tokenType) {
                case BEGIN_OBJECT:
                    checkExpectToken(tokenType, expectToken);
                    tokens.back();
                    array.add(parseJSONObject(tokens));
                    expectToken = TokenType.calcCode(SEP_COMMA, END_ARRAY);
                    break;
                case BEGIN_ARRAY:
                    checkExpectToken(tokenType, expectToken);
                    tokens.back();
                    array.add(parseJSONArray(tokens));
                    expectToken = TokenType.calcCode(SEP_COMMA, END_ARRAY);
                    break;
                case END_ARRAY:
                case END_DOCUMENT:
                    checkExpectToken(tokenType, expectToken);
                    return array;
                case NULL:
                    checkExpectToken(tokenType, expectToken);
                    array.add(null);
                    expectToken = TokenType.calcCode(SEP_COMMA, END_ARRAY);
                    break;
                case NUMBER:
                    checkExpectToken(tokenType, expectToken);
                    if (tokenValue.contains(".") || tokenValue.contains("e") || tokenValue.contains("E")) {
                        array.add(Double.valueOf(tokenValue));
                    } else {
                        long num = Long.parseLong(tokenValue);
                        if (num > Integer.MAX_VALUE || num < Integer.MIN_VALUE) {
                            array.add(num);
                        } else {
                            array.add((int) num);
                        }
                    }
                    expectToken = TokenType.calcCode(SEP_COMMA, END_ARRAY);
                    break;
                case BOOLEAN:
                    checkExpectToken(tokenType, expectToken);
                    array.add(Boolean.valueOf(tokenValue));
                    expectToken = TokenType.calcCode(SEP_COMMA, END_ARRAY);
                    break;
                case STRING:
                    checkExpectToken(tokenType, expectToken);
                    array.add(tokenValue);
                    expectToken = TokenType.calcCode(SEP_COMMA, END_ARRAY);
                    break;
                case SEP_COMMA:
                    checkExpectToken(tokenType, expectToken);
                    expectToken = TokenType.calcCode(STRING, NULL, NUMBER, BOOLEAN,
                            BEGIN_ARRAY, BEGIN_OBJECT);
                    break;
                default:
                    throw new JSONParseException("Unexpected Token.");
            }
        }

        throw new JSONParseException("Parse error, invalid Token.");
    }

    private void checkExpectToken(@NotNull TokenType type, int expectToken) {
        if ((type.code & expectToken) == 0) {
            throw new JSONParseException("Parse error, invalid Token.");
        }
    }
}
