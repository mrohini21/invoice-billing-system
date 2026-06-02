package com.company.invoice.util;

import java.time.LocalDateTime;

public class DateUtils {

    // ✅ Add days (for due date)
    public static LocalDateTime addDays(LocalDateTime date, int days) {
        return date.plusDays(days);
    }

    // ✅ Check if overdue
    public static boolean isOverdue(LocalDateTime dueDate) {
        return dueDate.isBefore(LocalDateTime.now());
    }

    // ✅ Get current timestamp
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}