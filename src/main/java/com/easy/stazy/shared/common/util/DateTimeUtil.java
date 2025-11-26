package com.easy.stazy.shared.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtil {
    public static LocalDateTime parseDateString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        dateStr = dateStr.trim();
        // Remove trailing 'Z' if present
        if (dateStr.endsWith("Z")) {
            dateStr = dateStr.substring(0, dateStr.length() - 1);
        }
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid date format. Please use ISO format: yyyy-MM-dd'T'HH:mm:ss[.SSS]");
        }
    }
}

