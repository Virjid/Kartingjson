package me.virjid.karting.json.parser;

/**
 * @author Virjid
 */
public class Token {
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


    private TokenType type;
    private String value;

    public Token(TokenType type, String value) {
        this.type  = type;
        this.value = value;
    }

    public TokenType type() {
        return type;
    }

    public void type(TokenType type) {
        this.type = type;
    }

    public String value() {
        return value;
    }

    public void value(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}
