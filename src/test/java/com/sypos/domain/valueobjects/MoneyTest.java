package com.sypos.domain.valueobjects;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void cannotBeNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new Money(new BigDecimal("-0.01")));
    }

    @Test
    void zeroIsAllowed() {
        Money m = new Money(new BigDecimal("0"));
        assertEquals(new BigDecimal("0.00"), m.getAmount());
    }

    @Test
    void setsScaleToTwoDp() {
        Money m = new Money(new BigDecimal("10"));
        assertEquals(new BigDecimal("10.00"), m.getAmount());
    }

    @Test
    void roundsHalfUpAtThirdDecimal_005RoundsUp() {
        Money m = new Money(new BigDecimal("1.005"));
        assertEquals(new BigDecimal("1.01"), m.getAmount());
    }

    @Test
    void roundsHalfUpAtThirdDecimal_004RoundsDown() {
        Money m = new Money(new BigDecimal("1.004"));
        assertEquals(new BigDecimal("1.00"), m.getAmount());
    }

    @Test
    void addProducesCorrectSum() {
        Money a = new Money(new BigDecimal("10.00"));
        Money b = new Money(new BigDecimal("2.50"));
        assertEquals(new BigDecimal("12.50"), a.add(b).getAmount());
    }

    @Test
    void addKeepsTwoDp() {
        Money a = new Money(new BigDecimal("0.10"));
        Money b = new Money(new BigDecimal("0.20"));
        assertEquals(new BigDecimal("0.30"), a.add(b).getAmount());
    }

    @Test
    void subtractProducesCorrectDifference() {
        Money a = new Money(new BigDecimal("10.00"));
        Money b = new Money(new BigDecimal("2.50"));
        assertEquals(new BigDecimal("7.50"), a.subtract(b).getAmount());
    }

    @Test
    void subtractToZeroIsAllowed() {
        Money a = new Money(new BigDecimal("5.00"));
        Money b = new Money(new BigDecimal("5.00"));
        assertEquals(new BigDecimal("0.00"), a.subtract(b).getAmount());
    }

    @Test
    void subtractThrowsIfInsufficient() {
        Money a = new Money(new BigDecimal("5.00"));
        Money b = new Money(new BigDecimal("5.01"));
        assertThrows(IllegalArgumentException.class, () -> a.subtract(b));
    }

    @Test
    void multiplyByZeroIsZero() {
        Money a = new Money(new BigDecimal("9.99"));
        assertEquals(new BigDecimal("0.00"), a.multiply(0).getAmount());
    }

    @Test
    void multiplyByOneIsSameAmount() {
        Money a = new Money(new BigDecimal("9.99"));
        assertEquals(new BigDecimal("9.99"), a.multiply(1).getAmount());
    }

    @Test
    void multiplyProducesCorrectResult() {
        Money a = new Money(new BigDecimal("3.33"));
        assertEquals(new BigDecimal("9.99"), a.multiply(3).getAmount());
    }

    @Test
    void equalsIsTrueForSameNumericValueAndScale() {
        Money a = new Money(new BigDecimal("10.0"));
        Money b = new Money(new BigDecimal("10.00"));
        // constructor normalizes to 2dp, so they should be equal
        assertEquals(a, b);
    }

    @Test
    void hashCodeMatchesForEqualObjects() {
        Money a = new Money(new BigDecimal("1.00"));
        Money b = new Money(new BigDecimal("1.0"));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void zeroFactoryCreatesZeroMoney() {
        Money z = Money.zero();
        assertEquals(new BigDecimal("0.00"), z.getAmount());
    }
}
