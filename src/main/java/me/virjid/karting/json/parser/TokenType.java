package me.virjid.karting.json.parser;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author Virjid
 */
public enum TokenType {
    BEGIN_OBJECT(1),
    END_OBJECT(2),
    BEGIN_ARRAY(4),
    END_ARRAY(8),
    NULL(16),
    NUMBER(32),
    STRING(64),
    BOOLEAN(128),
    SEP_COLON(256),
    SEP_COMMA(512),
    END_DOCUMENT(1024);

    TokenType(int code) {
        this.code = code;
    }

    public final int code;

    @Contract(pure = true)
    public static int calcCode(@NotNull TokenType... types) {
        int result = 0;

        for (TokenType type : types) {
            result |= type.code;
        }

        return result;
    }
}
