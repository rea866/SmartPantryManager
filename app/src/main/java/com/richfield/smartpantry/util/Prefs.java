package com.richfield.smartpantry.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Thin wrapper around SharedPreferences for the two Settings-screen toggles.
 */
public final class Prefs {

    private static final String FILE = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_IGNORE_EXPIRED = "ignore_expired";

    private Prefs() { }

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    /** Highlight items that expire soon (default: on). */
    public static boolean isExpiryAlertsEnabled(Context c) {
        return prefs(c).getBoolean(KEY_EXPIRY_ALERTS, true);
    }

    public static void setExpiryAlertsEnabled(Context c, boolean value) {
        prefs(c).edit().putBoolean(KEY_EXPIRY_ALERTS, value).apply();
    }

    /** Do not use expired items when suggesting recipes (default: on). */
    public static boolean isIgnoreExpired(Context c) {
        return prefs(c).getBoolean(KEY_IGNORE_EXPIRED, true);
    }

    public static void setIgnoreExpired(Context c, boolean value) {
        prefs(c).edit().putBoolean(KEY_IGNORE_EXPIRED, value).apply();
    }
}
