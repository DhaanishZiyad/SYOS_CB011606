package com.sypos.application.dto;

import com.sypos.domain.entities.Bill;
import com.sypos.domain.payment.PaymentResult;

import java.util.Objects;

public final class CheckoutResult {

    private final Bill bill;
    private final PaymentResult paymentResult;

    public CheckoutResult(Bill bill, PaymentResult paymentResult) {
        this.bill = Objects.requireNonNull(bill);
        this.paymentResult = Objects.requireNonNull(paymentResult);
    }

    public Bill getBill() {
        return bill;
    }

    public PaymentResult getPaymentResult() {
        return paymentResult;
    }
}
