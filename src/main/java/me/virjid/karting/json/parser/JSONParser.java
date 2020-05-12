package me.virjid.karting.json.parser;

import me.virjid.karting.json.exception.JSONParseException;
import me.virjid.karting.json.model.JSONArray;
import me.virjid.karting.json.model.JSONObject;
import org.jetbrains.annotations.NotNull;

/**
 * @author Virjid
 */
public class JSONParser {
    private static final int BEGIN_OBJECT_TOKEN = 1;
    private static final int END_OBJECT_TOKEN = 2;
    private static final int BEGIN_ARRAY_TOKEN = 4;
    private static final int END_ARRAY_TOKEN = 8;
    private static final int NULL_TOKEN = 16;
    private static final int NUMBER_TOKEN = 32;
    private static final int STRING_TOKEN = 64;
    private static final int BOOLEAN_TOKEN = 128;
    private static final int SEP_COLON_TOKEN = 256;
    private static final int SEP_COMMA_TOKEN = 512;

    @NotNull
    public JSONObject parseJSONObject(@NotNull TokenList tokens) {
        if (tokens.next().type() != TokenType.BEGIN_OBJECT) {
            throw new JSONParseException("Parse error, invalid Token.");
        }

        JSONObject JSONObject = new JSONObject();
        int expectToken = STRING_TOKEN | END_OBJECT_TOKEN;
        String key = null;
        Object value;
        while (tokens.hasMore()) {
            Token token = tokens.next();
            TokenType tokenType = token.type();
            String tokenValue = token.value();
            switch (tokenType) {
                case BEGIN_OBJECT:
                    checkExpectToken(tokenType, expectToken);
                    tokens.back();
                    JSONObject.put(key, parseJSONObject(tokens));
                    expectToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
                case END_OBJECT:
                case END_DOCUMENT:
                    checkExpectToken(tokenType, expectToken);
                    return JSONObject;
                case BEGIN_ARRAY:
                    checkExpectToken(tokenType, expectToken);
                    tokens.back();
                    JSONObject.put(key, parseJSONArray(tokens));
                    expectToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
                case NULL:
                    checkExpectToken(tokenType, expectToken);
                    JSONObject.put(key, null);
                    expectToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
                case NUMBER:
                    checkExpectToken(tokenType, expectToken);
                    if (tokenValue.contains(".") || tokenValue.contains("e") || tokenValue.contains("E")) {
                        JSONObject.put(key, Double.valueOf(tokenValue));
                    } else {
                        long num = Long.parseLong(tokenValue);
                        if (num > Integer.MAX_VALUE || num < Integer.MIN_VALUE) {
                            JSONObject.put(key, num);
                        } else {
                            JSONObject.put(key, (int) num);
                        }
                    }
                    expectToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
                case BOOLEAN:
                    checkExpectToken(tokenType, expectToken);
                    JSONObject.put(key, Boolean.valueOf(token.value()));
                    expectToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
                case STRING:
                    checkExpectToken(tokenType, expectToken);
                    Token preToken = tokens.peekPrevious();

                    if (preToken.type() == TokenType.SEP_COLON) {
                        value = token.value();
                        JSONObject.put(key, value);
                        expectToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    } else {
                        key = token.value();
                        expectToken = SEP_COLON_TOKEN;
                    }
                    break;
                case SEP_COLON:
                    checkExpectToken(tokenType, expectToken);
                    expectToken = NULL_TOKEN | NUMBER_TOKEN | BOOLEAN_TOKEN | STRING_TOKEN
                            | BEGIN_OBJECT_TOKEN | BEGIN_ARRAY_TOKEN;
                    break;
                case SEP_COMMA:
                    checkExpectToken(tokenType, expectToken);
                    expectToken = STRING_TOKEN;
                    break;
                default:
                    throw new JSONParseException("Unexpected Token.");
            }
        }

        throw new JSONParseException("Parse error, invalid Token.");
    }

    @NotNull
    public JSONArray parseJSONArray(@NotNull TokenList tokens) {
        if (tokens.next().type() != TokenType.BEGIN_ARRAY) {
            throw new JSONParseException("Parse error, invalid Token.");
        }

        int expectToken = BEGIN_ARRAY_TOKEN | END_ARRAY_TOKEN | BEGIN_OBJECT_TOKEN | NULL_TOKEN
                | NUMBER_TOKEN | BOOLEAN_TOKEN | STRING_TOKEN;
        JSONArray array = new JSONArray();
        while (tokens.hasMore()) {
            Token token = tokens.next();
            TokenType tokenType = token.type();
            String tokenValue   = token.value();
            switch (tokenType) {
                case BEGIN_OBJECT:
                    checkExpectToken(tokenType, expectToken);
                    tokens.back();
                    array.add(parseJSONObject(tokens));
                    expectToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case BEGIN_ARRAY:
                    checkExpectToken(tokenType, expectToken);
                    tokens.back();
                    array.add(parseJSONArray(tokens));
                    expectToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case END_ARRAY:
                case END_DOCUMENT:
                    checkExpectToken(tokenType, expectToken);
                    return array;
                case NULL:
                    checkExpectToken(tokenType, expectToken);
                    array.add(null);
                    expectToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
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
                    expectToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case BOOLEAN:
                    checkExpectToken(tokenType, expectToken);
                    array.add(Boolean.valueOf(tokenValue));
                    expectToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case STRING:
                    checkExpectToken(tokenType, expectToken);
                    array.add(tokenValue);
                    expectToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case SEP_COMMA:
                    checkExpectToken(tokenType, expectToken);
                    expectToken = STRING_TOKEN | NULL_TOKEN | NUMBER_TOKEN | BOOLEAN_TOKEN
                            | BEGIN_ARRAY_TOKEN | BEGIN_OBJECT_TOKEN;
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
