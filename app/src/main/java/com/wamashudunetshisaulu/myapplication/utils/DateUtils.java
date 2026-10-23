package com.wamashudunetshisaulu.myapplication.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** Small helpers for the optional expiry date, stored as ISO text "yyyy-MM-dd". */
public final class DateUtils {

    private DateUtils() { }

    /** Builds "2026-10-23" from date-picker values (month is 0-based like Calendar). */
    public static String toIso(int year, int monthZeroBased, int day) {
        return String.format(Locale.US, "%04d-%02d-%02d", year, monthZeroBased + 1, day);
    }

    /** Whole days from today until the expiry date (negative = already expired). Null if unparsable. */
    public static Integer daysUntil(String iso, Date today) {
        if (iso == null || iso.trim().isEmpty()) return null;
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            f.setLenient(false);
            Date expiry = f.parse(iso.trim());
            long diff = startOfDay(expiry) - startOfDay(today);
            return (int) Math.round(diff / 86400000.0); // rounding absorbs daylight-saving shifts
        } catch (ParseException e) {
            return null;
        }
    }

    private static long startOfDay(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
}
