package com.sypos.domain.payment;

import com.sypos.domain.valueobjects.Money;

import java.math.BigDecimal;
import java.util.Objects;

public class CashPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentResult pay(Money totalAmount, Money tenderedAmount) {
        Objects.requireNonNull(totalAmount);
        Objects.requireNonNull(tenderedAmount);

        BigDecimal total = totalAmount.getAmount();
        BigDecimal tendered = tenderedAmount.getAmount();

        if (tendered.compareTo(total) < 0) {
            throw new IllegalArgumentException("Tendered cash is less than the total amount due.");
        }

        Money change = new Money(tendered.subtract(total));
        return new PaymentResult(totalAmount, tenderedAmount, change);
    }
}
