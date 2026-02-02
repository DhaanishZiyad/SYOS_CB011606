package com.sypos.domain.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuantityTest {

    @Test
    void allowsZero() {
        Quantity q = new Quantity(0);
        assertEquals(0, q.getValue());
    }

    @Test
    void rejectsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity(-1));
    }

    @Test
    void acceptsPositiveOne() {
        Quantity q = new Quantity(1);
        assertEquals(1, q.getValue());
    }

    @Test
    void acceptsLargePositiveValue() {
        Quantity q = new Quantity(10_000);
        assertEquals(10_000, q.getValue());
    }

    @Test
    void equalsIsTrueForSameValue() {
        assertEquals(new Quantity(5), new Quantity(5));
    }

    @Test
    void equalsIsFalseForDifferentValues() {
        assertNotEquals(new Quantity(5), new Quantity(6));
    }

    @Test
    void equalsIsFalseForNull() {
        assertNotEquals(new Quantity(5), null);
    }

    @Test
    void hashCodeMatchesForEqualObjects() {
        Quantity a = new Quantity(7);
        Quantity b = new Quantity(7);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toStringIsNotEmpty() {
        String s = new Quantity(3).toString();
        assertNotNull(s);
        assertFalse(s.isBlank());
    }

    @Test
    void isImmutable_valueDoesNotChange() {
        Quantity q = new Quantity(4);
        // no setter exists; just assert value remains same
        assertEquals(4, q.getValue());
    }
}
