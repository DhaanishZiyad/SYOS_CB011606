package com.sypos.domain.payment;

import com.sypos.domain.valueobjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CashPaymentStrategyTest {

    private final CashPaymentStrategy strategy = new CashPaymentStrategy();

    @Test
    void throwsWhenTenderedIsLessThanTotal() {
        Money total = new Money(new BigDecimal("100.00"));
        Money tendered = new Money(new BigDecimal("99.99"));

        assertThrows(IllegalArgumentException.class, () -> strategy.pay(total, tendered));
    }

    @Test
    void allowsExactPaymentAndChangeIsZero() {
        Money total = new Money(new BigDecimal("100.00"));
        Money tendered = new Money(new BigDecimal("100.00"));

        var result = strategy.pay(total, tendered);

        assertEquals(new BigDecimal("100.00"), result.getTendered().getAmount());
        assertEquals(new BigDecimal("0.00"), result.getChange().getAmount());
    }

    @Test
    void calculatesCorrectChangeForOverpayment() {
        Money total = new Money(new BigDecimal("1150.00"));
        Money tendered = new Money(new BigDecimal("2000.00"));

        var result = strategy.pay(total, tendered);

        assertEquals(new BigDecimal("850.00"), result.getChange().getAmount());
    }

    @Test
    void worksWithSmallValues() {
        Money total = new Money(new BigDecimal("0.01"));
        Money tendered = new Money(new BigDecimal("0.01"));

        var result = strategy.pay(total, tendered);

        assertEquals(new BigDecimal("0.00"), result.getChange().getAmount());
    }

    @Test
    void changeIsRoundedToTwoDpBecauseMoneyIsRounded() {
        Money total = new Money(new BigDecimal("1.01"));
        Money tendered = new Money(new BigDecimal("2.005")); // becomes 2.01

        var result = strategy.pay(total, tendered);

        assertEquals(new BigDecimal("2.01"), result.getTendered().getAmount());
        assertEquals(new BigDecimal("1.00"), result.getChange().getAmount());
    }

    @Test
    void supportsLargeAmounts() {
        Money total = new Money(new BigDecimal("9999999.99"));
        Money tendered = new Money(new BigDecimal("10000000.00"));

        var result = strategy.pay(total, tendered);

        assertEquals(new BigDecimal("0.01"), result.getChange().getAmount());
    }

    @Test
    void changeIsNeverNegativeWhenTenderedIsGreaterThanOrEqualTotal() {
        Money total = new Money(new BigDecimal("10.00"));
        Money tendered = new Money(new BigDecimal("10.50"));

        var result = strategy.pay(total, tendered);

        assertTrue(result.getChange().getAmount().compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    void changeIsZeroWhenTenderedEqualsTotalEvenIfDifferentScale() {
        Money total = new Money(new BigDecimal("10.0"));
        Money tendered = new Money(new BigDecimal("10.00"));

        var result = strategy.pay(total, tendered);

        assertEquals(new BigDecimal("0.00"), result.getChange().getAmount());
    }

    @Test
    void tenderedRecordedExactlyAsMoneyAfterNormalization() {
        Money total = new Money(new BigDecimal("10.00"));
        Money tendered = new Money(new BigDecimal("10")); // normalizes to 10.00

        var result = strategy.pay(total, tendered);

        assertEquals(new BigDecimal("10.00"), result.getTendered().getAmount());
    }

    @Test
    void exactTenderedWithDecimalsNoChange() {
        Money total = new Money(new BigDecimal("10.25"));
        Money tendered = new Money(new BigDecimal("10.25"));

        var result = strategy.pay(total, tendered);

        assertEquals(new BigDecimal("0.00"), result.getChange().getAmount());
    }

    @Test
    void overpaymentWithDecimalsCorrectChange() {
        Money total = new Money(new BigDecimal("10.25"));
        Money tendered = new Money(new BigDecimal("20.00"));

        var result = strategy.pay(total, tendered);

        assertEquals(new BigDecimal("9.75"), result.getChange().getAmount());
    }

    @Test
    void throwsWhenTotalIsGreaterThanTenderedByOneCent() {
        Money total = new Money(new BigDecimal("10.01"));
        Money tendered = new Money(new BigDecimal("10.00"));

        assertThrows(IllegalArgumentException.class, () -> strategy.pay(total, tendered));
    }
}
