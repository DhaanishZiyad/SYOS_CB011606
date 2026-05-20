package com.sypos.concurrency;

import com.sypos.domain.entities.Bill;

import java.math.BigDecimal;

public record CheckoutTask(
        Bill bill,
        BigDecimal tendered
) {
}