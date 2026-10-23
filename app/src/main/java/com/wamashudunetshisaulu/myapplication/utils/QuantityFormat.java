package com.wamashudunetshisaulu.myapplication.utils;

import java.util.Locale;

/** Formats quantities for display: 3.0 -> "3", 0.5 -> "0.5". */
public final class QuantityFormat {

    private QuantityFormat() { }

    public static String number(double q) {
        if (q == Math.rint(q)) return String.valueOf((long) q);
        String s = String.format(Locale.US, "%.2f", q);
        if (s.endsWith("0")) s = s.substring(0, s.length() - 1);
        return s;
    }

    /** "500 g", or just "3" when the unit is empty. */
    public static String amount(double q, String unit) {
        String u = unit == null ? "" : unit.trim();
        return u.isEmpty() ? number(q) : number(q) + " " + u;
    }
}
