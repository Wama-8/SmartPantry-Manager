package com.wamashudunetshisaulu.myapplication.matching;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Converts quantities to a base unit so they can be compared.
 * Mass -> grams, volume -> millilitres, counts -> items.
 * Mass, volume and counts are NEVER converted into each other. A unit we do not
 * recognise is only comparable with the identical unit (conservative failure).
 */
public final class UnitConverter {

    /** Result of normalising a quantity: a dimension label and an amount in its base unit. */
    public static final class Amount {
        public final String dimension;
        public final double value;
        Amount(String dimension, double value) { this.dimension = dimension; this.value = value; }
    }

    private static final Map<String, Object[]> UNITS = new HashMap<>();

    static {
        put("mass", 1.0, "g", "gram", "grams");
        put("mass", 1000.0, "kg", "kilogram", "kilograms");
        put("mass", 28.3495, "oz", "ounce", "ounces");
        put("mass", 453.592, "lb", "lbs", "pound", "pounds");
        put("volume", 1.0, "ml", "millilitre", "millilitres", "milliliter", "milliliters");
        put("volume", 1000.0, "l", "litre", "litres", "liter", "liters");
        put("volume", 5.0, "tsp", "teaspoon", "teaspoons");
        put("volume", 15.0, "tbsp", "tablespoon", "tablespoons");
        put("volume", 250.0, "cup", "cups");
        put("count", 1.0, "", "piece", "pieces", "pc", "pcs", "whole", "item", "items", "unit", "units");
    }

    private UnitConverter() { }

    private static void put(String dimension, double factor, String... names) {
        for (String n : names) UNITS.put(n, new Object[]{dimension, factor});
    }

    /** Never returns null: unknown units become their own dimension ("other:<unit>"). */
    public static Amount toBase(double quantity, String unit) {
        String u = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT).replace(".", "");
        Object[] info = UNITS.get(u);
        if (info == null) return new Amount("other:" + u, quantity);
        return new Amount((String) info[0], quantity * (Double) info[1]);
    }
}
