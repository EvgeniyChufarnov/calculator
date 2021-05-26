package com.example.calculator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilTest {
    @Test
    public void formatResult_delete_zeros_after_numbers() {
        String testValue = "3.5550000";
        String result = Util.formatResult(testValue);
        assertEquals("3.555", result);
    }

    @Test
    public void formatResult_delete_zero_after_numbers() {
        String testValue = "3.5550";
        String result = Util.formatResult(testValue);
        assertEquals("3.555", result);
    }

    @Test
    public void formatResult_not_delete_zeros() {
        String testValue = "32200";
        String result = Util.formatResult(testValue);
        assertEquals("32200", result);
    }

    @Test
    public void formatResult_delete_zeros() {
        String testValue = "3.00";
        String result = Util.formatResult(testValue);
        assertEquals("3", result);
    }

    @Test
    public void formatResult_delete_point() {
        String testValue = "3.";
        String result = Util.formatResult(testValue);
        assertEquals("3", result);
    }

    @Test
    public void formatResult_short_format() {
        String testValue = "3.9765611862383703E18";
        String result = Util.formatResult(testValue);
        assertEquals("3.97656E18", result);
    }

    @Test
    public void formatResult_dont_delete() {
        String testValue = "3.973E10";
        String result = Util.formatResult(testValue);
        assertEquals("3.973E10", result);
    }

    @Test
    public void formatResult_dont_delete_zero() {
        String testValue = "100.0";
        String result = Util.formatResult(testValue);
        assertEquals("100", result);
    }
}