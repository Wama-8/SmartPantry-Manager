package com.wamashudunetshisaulu.myapplication.utils;

import java.util.Locale;

/** Small helpers for showing text nicely on screen. */
public final class TextFormat {

    private TextFormat() { }

    /** "cheese" -> "Cheese", "red onion" -> "Red onion". Empty or null becomes "". */
    public static String capitalize(String s) {
        if (s == null) return "";
        String t = s.trim();
        if (t.isEmpty()) return "";
        return t.substring(0, 1).toUpperCase(Locale.ROOT) + t.substring(1);
    }

    /** First letter for the round avatar, "?" when the name is empty. */
    public static String initial(String s) {
        String c = capitalize(s);
        return c.isEmpty() ? "?" : c.substring(0, 1);
    }

    /** plural(1, "ingredient", "ingredients") -> "1 ingredient"; plural(3, ...) -> "3 ingredients". */
    public static String count(int n, String singular, String plural) {
        return n + " " + (n == 1 ? singular : plural);
    }
}
