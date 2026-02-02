package com.sypos.application.dto;

import com.sypos.domain.entities.Bill;
import com.sypos.domain.payment.PaymentResult;
import com.sypos.domain.valueobjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutResultTest {

    @Test
    void exposesBillAndPaymentResult() {
        Bill bill = new Bill(1, LocalDate.now());
        PaymentResult payment = new PaymentResult(
                new Money(new BigDecimal("100.00")),
                new Money(new BigDecimal("200.00")),
                new Money(new BigDecimal("100.00"))
        );

        CheckoutResult result = new CheckoutResult(bill, payment);

        assertSame(bill, result.getBill());
        assertSame(payment, result.getPaymentResult());
    }
}
