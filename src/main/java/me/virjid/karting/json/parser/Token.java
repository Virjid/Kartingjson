package me.virjid.karting.json.parser;

/**
 * @author Virjid
 */
public class Token {

    // ----------------------------------------
    // 定义一些值是固定不变的Token
    // ----------------------------------------
    public static final Token BEGIN_OBJECT_TOKEN = new Token(TokenType.BEGIN_OBJECT, "{");
    public static final Token END_OBJECT_TOKEN   = new Token(TokenType.END_OBJECT,   "}");
    public static final Token BEGIN_ARRAY_TOKEN  = new Token(TokenType.BEGIN_ARRAY,  "[");
    public static final Token END_ARRAY_TOKEN    = new Token(TokenType.END_ARRAY,    "]");
    public static final Token NULL_TOKEN         = new Token(TokenType.NULL,         "null");
    public static final Token BOOL_TRUE_TOKEN    = new Token(TokenType.BOOLEAN,      "true");
    public static final Token BOOL_FALSE_TOKEN   = new Token(TokenType.BOOLEAN,      "false");
    public static final Token SEP_COLON_TOKEN    = new Token(TokenType.SEP_COLON,    ":");
    public static final Token SEP_COMMA_TOKEN    = new Token(TokenType.SEP_COMMA,    ",");
    public static final Token END_DOCUMENT_TOKEN = new Token(TokenType.END_DOCUMENT, null);


    private final TokenType type;
    private final String value;

    public Token(final TokenType type, final String value) {
        this.type  = type;
        this.value = value;
    }

    public TokenType type() {
        return type;
    }
    public String value() {
        return value;
    }


    @Override
    public String toString() {
        return "Token <" + type + ", " + value + ">;";
    }
}
