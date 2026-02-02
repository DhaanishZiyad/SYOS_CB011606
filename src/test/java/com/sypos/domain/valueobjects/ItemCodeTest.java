package com.sypos.domain.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemCodeTest {

    @Test
    void cannotBeNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemCode(null));
    }

    @Test
    void cannotBeEmptyString() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemCode(""));
    }

    @Test
    void cannotBeBlankString() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemCode("   "));
    }

    @Test
    void acceptsValidCode() {
        ItemCode code = new ItemCode("MILK001");
        assertEquals("MILK001", code.getValue());
    }

    @Test
    void acceptsCodeWithNumbersAndLetters() {
        ItemCode code = new ItemCode("A12B34");
        assertEquals("A12B34", code.getValue());
    }

    @Test
    void equalsTrueForSameValue() {
        ItemCode a = new ItemCode("MILK001");
        ItemCode b = new ItemCode("MILK001");

        assertEquals(a, b);
    }

    @Test
    void equalsFalseForDifferentValues() {
        ItemCode a = new ItemCode("MILK001");
        ItemCode b = new ItemCode("BREAD001");

        assertNotEquals(a, b);
    }

    @Test
    void equalsIsCaseSensitive() {
        ItemCode upper = new ItemCode("MILK001");
        ItemCode lower = new ItemCode("milk001");

        assertNotEquals(upper, lower);
    }

    @Test
    void hashCodeMatchesForEqualObjects() {
        ItemCode a = new ItemCode("MILK001");
        ItemCode b = new ItemCode("MILK001");

        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toStringReturnsValue() {
        ItemCode code = new ItemCode("MILK001");
        assertEquals("MILK001", code.toString());
    }
}
