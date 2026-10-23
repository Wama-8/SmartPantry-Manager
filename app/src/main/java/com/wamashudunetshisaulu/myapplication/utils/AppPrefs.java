package com.wamashudunetshisaulu.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

/** The app's one user setting: how many days before expiry an item is flagged "expiring soon". */
public final class AppPrefs {

    private static final String FILE = "smartpantry_prefs";
    private static final String KEY_WARN_DAYS = "expiry_warning_days";
    public static final int DEFAULT_WARN_DAYS = 3;

    private AppPrefs() { }

    public static int getExpiryWarningDays(Context context) {
        SharedPreferences p = context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        return p.getInt(KEY_WARN_DAYS, DEFAULT_WARN_DAYS);
    }

    public static void setExpiryWarningDays(Context context, int days) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putInt(KEY_WARN_DAYS, days).apply();
    }
}
