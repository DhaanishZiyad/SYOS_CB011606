package com.sypos.domain.payment;

import com.sypos.domain.valueobjects.Money;

public interface PaymentStrategy {
    PaymentResult pay(Money totalAmount, Money tenderedAmount);
}
