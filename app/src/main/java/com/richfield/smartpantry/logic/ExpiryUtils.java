package com.richfield.smartpantry.logic;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Helpers for working with the yyyy-MM-dd expiry dates stored in the database.
 */
public final class ExpiryUtils {

    /** Items expiring within this many days count as "expiring soon". */
    public static final int SOON_DAYS = 3;

    private ExpiryUtils() { }

    /** Parses an ISO date, returning null for empty or invalid input. */
    public static LocalDate parse(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(s.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** Days from today until the expiry date (negative = already expired); null if no date. */
    public static Long daysUntil(String expiry, LocalDate today) {
        LocalDate d = parse(expiry);
        return d == null ? null : ChronoUnit.DAYS.between(today, d);
    }

    public static boolean isExpired(String expiry, LocalDate today) {
        Long days = daysUntil(expiry, today);
        return days != null && days < 0;
    }
}
