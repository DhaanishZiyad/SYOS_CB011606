package com.sypos.application.dto.reports;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StockBatchLineTest {

    @Test
    void createsStockBatchLineCorrectly() {
        LocalDate purchaseDate = LocalDate.of(2026, 1, 1);
        LocalDate expiryDate = LocalDate.of(2026, 2, 1);

        StockBatchLine line = new StockBatchLine(
                "MILK001",
                purchaseDate,
                100,
                80,
                expiryDate
        );

        assertEquals("MILK001", line.getItemCode());
        assertEquals(purchaseDate, line.getPurchaseDate());
        assertEquals(100, line.getReceivedQuantity());
        assertEquals(80, line.getRemainingQuantity());
        assertEquals(expiryDate, line.getExpiryDate());
    }
}
