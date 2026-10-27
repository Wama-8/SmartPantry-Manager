package com.wamashudunetshisaulu.myapplication.utils;

import java.util.ArrayList;
import java.util.List;

/** Splits stored preparation text ("1. Do this\n2. Do that") into clean steps without the numbers. */
public final class StepParser {

    private StepParser() { }

    public static List<String> split(String instructions) {
        List<String> steps = new ArrayList<>();
        if (instructions == null) return steps;
        for (String line : instructions.split("\\r?\\n")) {
            String s = line.trim().replaceFirst("^\\d+[.)]\\s*", "");
            if (!s.isEmpty()) steps.add(s);
        }
        return steps;
    }
}
