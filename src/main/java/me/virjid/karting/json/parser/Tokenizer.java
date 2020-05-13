package me.virjid.karting.json.parser;

import me.virjid.karting.json.exception.JSONParseException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author Virjid
 */
public class Tokenizer {
    private CharReader reader;

    public TokenList tokenize(CharReader reader) throws IOException {
        this.reader = reader;
        TokenList tokens = new TokenList();
        tokenize(tokens);
        return tokens;
    }

    private void tokenize(@NotNull TokenList tokens) throws IOException {
        Token token;
        do {
            token = readToken();
            tokens.add(token);
        } while (token.type() != TokenType.END_DOCUMENT);
    }

    // get next token
    private Token readToken() throws IOException {
        // skip whitespace characters
        reader.skipWhite();

        // if hasn't next character
        if (!reader.hasNext()) {
            return Token.END_DOCUMENT;
        }

        // read next character
        char c = reader.next();

        if (c == '{') return Token.BEGIN_OBJECT;
        if (c == '}') return Token.END_OBJECT;
        if (c == '[') return Token.BEGIN_ARRAY;
        if (c == ']') return Token.END_ARRAY;
        if (c == ':') return Token.SEP_COLON;
        if (c == ',') return Token.SEP_COMMA;

        // null or undefined
        if (c == 'n' || c == 'u') return readNull();

        // true or false
        if (c == 't' || c == 'f') return readBoolean();

        // handle string
        if (c == '"' || c == '\'') return readString(c);

        // handle number
        if (c == '-' || c == '.' || Character.isDigit(c))
            return readNumber();

        // error
        throw new JSONParseException("Illegal character => " + c);
    }

    @NotNull
    @Contract(" -> new")
    private Token readNull() throws IOException {
        char c = reader.peek();

        // is null?
        if (c == 'n' && !reader.matchNextAndSkip("ull")) {
            throw new JSONParseException("Invalid json string");
        }

        // is undefined?
        if (c == 'u' && !reader.matchNextAndSkip("ndefined")) {
            throw new JSONParseException("Invalid json string");
        }

        return Token.NULL;
    }

    private Token readBoolean() throws IOException {
        char c = reader.peek();

        // is true?
        if (c == 't') {
            if (!reader.matchNextAndSkip("rue")) {
                throw new JSONParseException("Invalid json string");
            }
            return Token.BOOL_TRUE;
        }
        // is false?
        else {
            if (!reader.matchNextAndSkip("alse")) {
                throw new JSONParseException("Invalid json string");
            }
            return Token.BOOL_FALSE;
        }
    }

    @NotNull
    private Token readString(final char quote) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char ch = reader.next();

            // 处理转义字符
            if (ch == '\\') {
                ch = reader.next();
                if (!isEscape(ch)) {
                    throw new JSONParseException("Invalid escape character");
                }

                // handle unicode
                if (ch == 'u') {
                    StringBuilder unicode = new StringBuilder();
                    for (int i = 0; i < 4; i++) {
                        ch = reader.next();
                        if (isHex(ch)) {
                            unicode.append(ch);
                        } else {
                            throw new JSONParseException("Invalid character");
                        }
                    }
                    sb.append((char) Integer.valueOf(unicode.toString(), 16).intValue());
                }

                else {
                    sb.append(ch);
                }
            }

            // the end of parsing string
            else if (ch == quote || ch == CharReader.EOF) {
                return new Token(TokenType.STRING, sb.toString());
            }

            // 字符串中途不允许回车，回车必须使用转义字符
            else if (ch == '\r' || ch == '\n') {
                throw new JSONParseException("Invalid character");
            }
            // 添加字符
            else {
                sb.append(ch);
            }
        }
    }

    @NotNull
    @Contract(" -> new")
    private Token readNumber() throws IOException {
        char ch = reader.peek();

        StringBuilder sb = new StringBuilder();

        // handle minus number
        if (ch == '-') {
            sb.append(ch);

            ch = reader.next();
            if (ch == '0' || ch == '.') {    // -0.xxxx or -.xxxx
                sb.append('0');
                if (ch == '.') reader.back();
                sb.append(readFracAndExp());
            } else if (Character.isDigit(ch)) {
                do {
                    sb.append(ch);
                    ch = reader.next();
                } while (Character.isDigit(ch));

                if (ch != CharReader.EOF) {
                    reader.back();
                    sb.append(readFracAndExp());
                }
            } else {
                throw new JSONParseException("Invalid minus number");
            }
        }

        else if (ch == '0' || ch == '.') {
            sb.append('0');
            if (ch == '.') reader.back();
            sb.append(readFracAndExp());
        }

        else {
            do {
                sb.append(ch);
                ch = reader.next();
            } while (Character.isDigit(ch));
            if (ch != CharReader.EOF) {
                reader.back();
                sb.append(readFracAndExp());
            }
        }

        return new Token(TokenType.NUMBER, sb.toString());
    }

    private boolean isEscape(char ch) {
        return (ch == '"' || ch == '\'' || ch == '\\' || ch == 'u' || ch == 'r'
                || ch == 'n' || ch == 'b' || ch == 't' || ch == 'f');
    }

    private boolean isHex(char c) {
        return ((c >= '0' && c <= '9') || ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F'));
    }

    @NotNull
    private String readFracAndExp() throws IOException {
        StringBuilder sb = new StringBuilder();
        char c = reader.next();
        if (c ==  '.') {
            sb.append(c);
            c = reader.next();
            if (!Character.isDigit(c)) {
                throw new JSONParseException("Invalid frac");
            }

            do {
                sb.append(c);
                c = reader.next();
            } while (Character.isDigit(c));

            if (c == 'e' || c == 'E') {
                sb.append(c);
                sb.append(readExp());
            } else {
                if (c != CharReader.EOF) reader.back();
            }
        } else if (c == 'e' || c == 'E') {
            sb.append(c);
            sb.append(readExp());
        } else {
            reader.back();
        }

        return sb.toString();
    }

    @NotNull
    private String readExp() throws IOException {
        StringBuilder sb = new StringBuilder();
        char c = reader.next();
        if (Character.isDigit(c) || c == '-' || c =='+') {
            if (c == '-' || c == '+') {
                sb.append(c);
                c = reader.next();
            }

            if (Character.isDigit(c)) {
                do {
                    sb.append(c);
                    c = reader.next();
                } while (Character.isDigit(c));

                if (c != CharReader.EOF) {
                    reader.back();
                }
            }

            else {
                throw new JSONParseException("Exponential partial parsing error");
            }
        } else {
            throw new JSONParseException("Exponential partial parsing error");
        }

        return sb.toString();
    }
}
