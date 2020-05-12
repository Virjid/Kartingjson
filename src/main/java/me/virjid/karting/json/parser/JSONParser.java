package me.virjid.karting.json.parser;

import me.virjid.karting.json.exception.JSONParseException;
import me.virjid.karting.json.model.JSONArray;
import me.virjid.karting.json.model.JSONObject;
import org.jetbrains.annotations.NotNull;

import static me.virjid.karting.json.parser.TokenType.*;

/**
 * @author Virjid
 */
public class JSONParser {

    @NotNull
    public JSONObject parseJSONObject(@NotNull TokenList tokens) {
        if (tokens.next().type() != BEGIN_OBJECT) {
            throw new JSONParseException("Parse error, invalid Token.");
        }

        JSONObject JSONObject = new JSONObject();
        int expectToken = TokenType.calcCode(STRING, END_OBJECT);
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
                    expectToken = TokenType.calcCode(SEP_COMMA, END_OBJECT);
                    break;
                case END_OBJECT:
                case END_DOCUMENT:
                    checkExpectToken(tokenType, expectToken);
                    return JSONObject;
                case BEGIN_ARRAY:
                    checkExpectToken(tokenType, expectToken);
                    tokens.back();
                    JSONObject.put(key, parseJSONArray(tokens));
                    expectToken = TokenType.calcCode(SEP_COMMA, END_OBJECT);
                    break;
                case NULL:
                    checkExpectToken(tokenType, expectToken);
                    JSONObject.put(key, null);
                    expectToken = TokenType.calcCode(SEP_COMMA, END_OBJECT);
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
                    expectToken =TokenType.calcCode(SEP_COMMA, END_OBJECT);
                    break;
                case BOOLEAN:
                    checkExpectToken(tokenType, expectToken);
                    JSONObject.put(key, Boolean.valueOf(token.value()));
                    expectToken = TokenType.calcCode(SEP_COMMA, END_OBJECT);
                    break;
                case STRING:
                    checkExpectToken(tokenType, expectToken);
                    Token preToken = tokens.peekPrevious();

                    if (preToken.type() == SEP_COLON) {
                        value = token.value();
                        JSONObject.put(key, value);
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
        while (tokens.hasMore()) {
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
