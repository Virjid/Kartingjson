package me.virjid.karting.json.util;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * @author Virjid
 */
public class StringUtil {
    // --------------------------------------
    // 默认的时间格式化器
    // --------------------------------------
    private static final DateTimeFormatter DATE_FORMAT         = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER      = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String dateTimeToString(TemporalAccessor obj) {
        if (obj == null) return null;

        // LocalDateTime
        if (obj instanceof LocalDateTime) {
            return DATE_TIME_FORMATTER.format(obj);
        }
        // LocalDate
        else if (obj instanceof LocalDate) {
            return DATE_FORMAT.format(obj);
        }
        // LocalTime
        else if (obj instanceof LocalTime) {
            return TIME_FORMATTER.format(obj);
        }

        return "";
    }

    @NotNull
    public static String dateTimeToString(TemporalAccessor obj, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        return formatter.format(obj);
    }
}
