package me.virjid.karting.json.parser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Virjid
 */
public class CharReader {
    public static final char EOF = (char) -1;

    private Reader reader;
    private char[] buffer;

    /**
     * pos的取值区间为 [-1, size-1].
     * 当pos为-1时表示一个字符都还未被读取；当pos为size-1时则表示已经全部读取完毕
     */
    private int pos;
    private int size;

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    public CharReader(Reader reader, int initSize) {
        this.reader = reader;
        this.buffer = new char[initSize];
        pos  = -1;
        size = 0;
    }

    public CharReader(Reader reader) {
        this(reader, DEFAULT_BUFFER_SIZE);
    }

    // 推进pos，获取下一个字符
    public char next() throws IOException {
        if (!hasNext()) {
            return EOF;
        }

        return buffer[++pos];
    }

    // 判断是否还有可读的字符
    public boolean hasNext() throws IOException {
        if (pos < size - 1) {
            return true;
        }

        // 填充缓冲池
        int n = reader.read(buffer);
        if (n == -1) return false;

        // 指针复位、大小重置
        pos  = -1;
        size = n;

        return pos < size - 1;
    }

    // 重新获取前一次获取过的字符，这不会对pos的值造成影响
    public char peek() {
        if (pos >= size) {
            return EOF;
        }

        return buffer[Math.max(0, pos)];
    }

    // 滑过空白字符
    public void skipWhite() throws IOException {
        char c = next();

        while (Character.isWhitespace(c)) {
            c = next();
        }

        if (c != EOF) back();
    }

    // 回退一个字符位置
    public void back() {
        pos = Math.max(--pos, -1); // pos至少为-1
    }


    public boolean matchNextAndSkip(@NotNull String match) throws IOException {

        for (int i = 0, size = match.length(); i < size; i++) {
            if (!hasNext()) return false;

            char c = next();
            char m = match.charAt(i);
            if (c != m) {
                return false;
            }
        }

        return true;
    }
}
