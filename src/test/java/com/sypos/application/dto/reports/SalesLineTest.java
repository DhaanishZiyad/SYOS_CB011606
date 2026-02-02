package com.sypos.application.dto.reports;

import com.sypos.domain.valueobjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SalesLineTest {

    @Test
    void createsSalesLineCorrectly() {
        Money revenue = new Money(new BigDecimal("500.00"));

        SalesLine line = new SalesLine(
                "MILK001",
                "Milk 1L",
                5,
                revenue
        );

        assertEquals("MILK001", line.getItemCode());
        assertEquals("Milk 1L", line.getItemName());
        assertEquals(5, line.getTotalQuantity());
        assertEquals(new BigDecimal("500.00"), line.getTotalRevenue().getAmount());
    }
}
