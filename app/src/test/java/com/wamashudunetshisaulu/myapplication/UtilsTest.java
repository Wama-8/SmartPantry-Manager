package com.wamashudunetshisaulu.myapplication;

import static org.junit.Assert.*;

import com.wamashudunetshisaulu.myapplication.utils.DateUtils;
import com.wamashudunetshisaulu.myapplication.utils.InputValidator;
import com.wamashudunetshisaulu.myapplication.utils.QuantityFormat;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class UtilsTest {

    private static Date day(int y, int m1, int d) {
        Calendar c = Calendar.getInstance();
        c.set(y, m1 - 1, d, 15, 30, 0);
        return c.getTime();
    }

    @Test public void emptyNameRejected() {
        assertNotNull(InputValidator.validateName(""));
        assertNotNull(InputValidator.validateName("   "));
        assertNotNull(InputValidator.validateName("123"));
        assertNull(InputValidator.validateName("Flour"));
    }

    @Test public void quantityRules() {
        assertNotNull(InputValidator.validateQuantity(""));
        assertNotNull(InputValidator.validateQuantity("abc"));
        assertNotNull(InputValidator.validateQuantity("0"));
        assertNotNull(InputValidator.validateQuantity("-5"));
        assertNotNull(InputValidator.validateQuantity("NaN"));
        assertNotNull(InputValidator.validateQuantity("1e9"));
        assertNull(InputValidator.validateQuantity("250"));
        assertNull(InputValidator.validateQuantity("1,5"));
        assertEquals(1.5, InputValidator.parseQuantity("1,5"), 1e-9);
    }

    @Test public void expiryDays() {
        Date today = day(2026, 10, 23);
        assertEquals(0, (int) DateUtils.daysUntil("2026-10-23", today));
        assertEquals(3, (int) DateUtils.daysUntil("2026-10-26", today));
        assertEquals(-1, (int) DateUtils.daysUntil("2026-10-22", today));
        assertNull(DateUtils.daysUntil("", today));
        assertNull(DateUtils.daysUntil(null, today));
        assertNull(DateUtils.daysUntil("2026-13-40", today));
        assertEquals("2026-01-05", DateUtils.toIso(2026, 0, 5));
    }

    @Test public void quantityFormatting() {
        assertEquals("3", QuantityFormat.number(3.0));
        assertEquals("0.5", QuantityFormat.number(0.5));
        assertEquals("0.25", QuantityFormat.number(0.25));
        assertEquals("500 g", QuantityFormat.amount(500, "g"));
        assertEquals("3", QuantityFormat.amount(3, ""));
    }
}
