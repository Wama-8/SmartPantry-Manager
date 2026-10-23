package com.wamashudunetshisaulu.myapplication.utils;

/** Validation rules for the Add/Edit Ingredient form. Returns an error message, or null when valid. */
public final class InputValidator {

    public static final int MAX_NAME_LENGTH = 50;
    public static final double MAX_QUANTITY = 100000;

    private InputValidator() { }

    public static String validateName(String raw) {
        String s = raw == null ? "" : raw.trim();
        if (s.isEmpty()) return "Please enter an ingredient name.";
        if (s.length() > MAX_NAME_LENGTH) return "Name is too long (max " + MAX_NAME_LENGTH + " characters).";
        boolean hasLetter = false;
        for (int i = 0; i < s.length(); i++) if (Character.isLetter(s.charAt(i))) { hasLetter = true; break; }
        if (!hasLetter) return "Name must contain letters.";
        return null;
    }

    /** Accepts "1.5" and "1,5" (comma decimal). Returns null when it is not a usable number. */
    public static Double parseQuantity(String raw) {
        if (raw == null) return null;
        String s = raw.trim().replace(',', '.');
        if (s.isEmpty()) return null;
        try {
            double v = Double.parseDouble(s);
            if (Double.isNaN(v) || Double.isInfinite(v)) return null;
            return v;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String validateQuantity(String raw) {
        Double v = parseQuantity(raw);
        if (v == null) return "Please enter a number, e.g. 250 or 1.5.";
        if (v <= 0) return "Quantity must be greater than zero.";
        if (v > MAX_QUANTITY) return "Quantity is too large.";
        return null;
    }
}
