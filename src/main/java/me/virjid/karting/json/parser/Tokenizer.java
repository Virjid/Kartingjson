package me.virjid.karting.json.parser;

import me.virjid.karting.json.exception.JSONParseException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;

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
        // 使用do-while处理空文件
        Token token;
        do {
            token = readToken();
            tokens.add(token);
        } while (token.type() != TokenType.END_DOCUMENT);
    }

    // 读取下一个Token
    private Token readToken() throws IOException {
        reader.skipWhite();

        if (!reader.hasNext()) {
            return Token.END_DOCUMENT_TOKEN;
        }

        char c = reader.next();

        if (c == '{') return Token.BEGIN_OBJECT_TOKEN;
        if (c == '}') return Token.END_OBJECT_TOKEN;
        if (c == '[') return Token.BEGIN_ARRAY_TOKEN;
        if (c == ']') return Token.END_ARRAY_TOKEN;
        if (c == ':') return Token.SEP_COLON_TOKEN;
        if (c == ',') return Token.SEP_COMMA_TOKEN;

        // 处理null和undefined
        if (c == 'n' || c == 'u') return readNull();

        // 处理true和false
        if (c == 't' || c == 'f') return readBoolean();

        // 处理字符串
        if (c == '"') return readString();

        // 处理数字
        if (c == '-' || c == '.' || Character.isDigit(c))
            return readNumber();

        throw new JSONParseException("Illegal character");
    }

    @NotNull
    @Contract(" -> new")
    private Token readNull() throws IOException {
        char c = reader.peek();

        if (c == 'n' && !reader.matchNextAndSkip("ull")) {
            throw new JSONParseException("Invalid json string");
        }

        if (c == 'u' && !reader.matchNextAndSkip("ndefined")) {
            throw new JSONParseException("Invalid json string");
        }

        return Token.NULL_TOKEN;
    }

    private Token readBoolean() throws IOException {
        if (reader.peek() == 't') {
            if (!(reader.next() == 'r' && reader.next() == 'u' && reader.next() == 'e')) {
                throw new JSONParseException("Invalid json string");
            }

            return Token.BOOL_TRUE_TOKEN;
        } else {
            if (!(reader.next() == 'a' && reader.next() == 'l'
                    && reader.next() == 's' && reader.next() == 'e')) {
                throw new JSONParseException("Invalid json string");
            }

            return Token.BOOL_FALSE_TOKEN;
        }
    }

    private Token readString() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char ch = reader.next();

            // 处理转义字符
            if (ch == '\\') {
                if (!isEscape()) {
                    throw new JSONParseException("Invalid escape character");
                }
                sb.append('\\');
                ch = reader.peek();
                sb.append(ch);

                if (ch == 'u') {
                    for (int i = 0; i < 4; i++) {
                        ch = reader.next();
                        if (isHex(ch)) {
                            sb.append(ch);
                        } else {
                            throw new JSONParseException("Invalid character");
                        }
                    }
                }
            }
            // 字符串解析结束
            else if (ch == '"') {
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

    private Token readNumber() throws IOException {
        // 开头字符
        char ch = reader.peek();

        StringBuilder sb = new StringBuilder();

        if (ch == '-') {    // 处理负数
            sb.append(ch);
            ch = reader.next();
            if (ch == '0' || ch == '.') {    // 处理 -0.xxxx 或 -.xxxx
                sb.append(ch);
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
        } else if (ch == '0' || ch == '.') {    // 处理小数
            sb.append('0');
            if (ch == '.') reader.back();
            sb.append(readFracAndExp());
        } else {
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

    private boolean isEscape() throws IOException {
        char ch = reader.next();
        return (ch == '"' || ch == '\\' || ch == 'u' || ch == 'r'
                || ch == 'n' || ch == 'b' || ch == 't' || ch == 'f');
    }

    private boolean isHex(char ch) {
        return ((ch >= '0' && ch <= '9') || ('a' <= ch && ch <= 'f')
                || ('A' <= ch && ch <= 'F'));
    }

    private String readFracAndExp() throws IOException {
        StringBuilder sb = new StringBuilder();
        char ch = reader.next();
        if (ch ==  '.') {
            sb.append(ch);
            ch = reader.next();
            if (!Character.isDigit(ch)) {
                throw new JSONParseException("Invalid frac");
            }
            do {
                sb.append(ch);
                ch = reader.next();
            } while (Character.isDigit(ch));

            if (isExp(ch)) {    // 处理科学计数法
                sb.append(ch);
                sb.append(readExp());
            } else {
                if (ch != (char) -1) {
                    reader.back();
                }
            }
        } else if (isExp(ch)) {
            sb.append(ch);
            sb.append(readExp());
        } else {
            reader.back();
        }

        return sb.toString();
    }

    private String readExp() throws IOException {
        StringBuilder sb = new StringBuilder();
        char ch = reader.next();
        if (ch == '+' || ch =='-') {
            sb.append(ch);
            ch = reader.next();
            if (Character.isDigit(ch)) {
                do {
                    sb.append(ch);
                    ch = reader.next();
                } while (Character.isDigit(ch));

                if (ch != (char) -1) {    // 读取结束，不用回退
                    reader.back();
                }
            } else {
                throw new JSONParseException("e or E");
            }
        } else {
            throw new JSONParseException("e or E");
        }

        return sb.toString();
    }

    private boolean isExp(char ch) {
        return ch == 'e' || ch == 'E';
    }

    public static void main(String[] args) throws IOException {
        String str = "{\"a\": \"\\uAFFF\", \"b\": null}";

        InputStream inputStream = new ByteArrayInputStream(str.getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        Tokenizer tokenizer = new Tokenizer();
        TokenList tokens = tokenizer.tokenize(new CharReader(reader));

        System.out.println(tokens);
    }
}
