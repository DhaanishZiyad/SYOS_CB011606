package com.sypos.domain.payment;

import com.sypos.domain.valueobjects.Money;

import java.util.Objects;

public final class PaymentResult {

    private final Money total;
    private final Money tendered;
    private final Money change;

    public PaymentResult(Money total, Money tendered, Money change) {
        this.total = Objects.requireNonNull(total);
        this.tendered = Objects.requireNonNull(tendered);
        this.change = Objects.requireNonNull(change);
    }

    public Money getTotal() {
        return total;
    }

    public Money getTendered() {
        return tendered;
    }

    public Money getChange() {
        return change;
    }
}
