package com.sypos.domain.entities;

import com.sypos.domain.valueobjects.ItemCode;
import com.sypos.domain.valueobjects.Quantity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StockBatchTest {

    private StockBatch batch(int qty) {
        return new StockBatch(
                1L,
                new ItemCode("MILK001"),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 2, 1),
                new Quantity(qty)
        );
    }

    @Test
    void createsStockBatchWithCorrectInitialValues() {
        StockBatch batch = new StockBatch(
                99L,
                new ItemCode("BREAD001"),
                LocalDate.of(2026, 1, 10),
                LocalDate.of(2026, 2, 20),
                new Quantity(50)
        );

        assertEquals(99L, batch.getId());
        assertEquals(new ItemCode("BREAD001"), batch.getItemCode());
        assertEquals(LocalDate.of(2026, 1, 10), batch.getPurchaseDate());
        assertEquals(LocalDate.of(2026, 2, 20), batch.getExpiryDate());
        assertEquals(50, batch.getQuantity().getValue());
    }

    @Test
    void reduceQuantityDecreasesQuantity() {
        StockBatch batch = batch(10);

        batch.reduceQuantity(new Quantity(3));

        assertEquals(7, batch.getQuantity().getValue());
    }

    @Test
    void reduceQuantityToZeroIsAllowed() {
        StockBatch batch = batch(5);

        batch.reduceQuantity(new Quantity(5));

        assertEquals(0, batch.getQuantity().getValue());
    }

    @Test
    void reduceQuantityWithZeroDoesNothing() {
        StockBatch batch = batch(8);

        batch.reduceQuantity(new Quantity(0));

        assertEquals(8, batch.getQuantity().getValue());
    }

    @Test
    void reduceQuantityMoreThanAvailableThrows() {
        StockBatch batch = batch(4);

        assertThrows(IllegalArgumentException.class,
                () -> batch.reduceQuantity(new Quantity(10)));
    }

    @Test
    void reduceQuantityRejectsNull() {
        StockBatch batch = batch(5);

        assertThrows(NullPointerException.class,
                () -> batch.reduceQuantity(null));
    }

    @Test
    void itemCodeDoesNotChangeWhenReducingQuantity() {
        ItemCode code = new ItemCode("MILK001");
        StockBatch batch = new StockBatch(
                1L,
                code,
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                new Quantity(10)
        );

        batch.reduceQuantity(new Quantity(2));

        assertEquals(code, batch.getItemCode());
    }

    @Test
    void datesRemainUnchangedAfterReduction() {
        LocalDate purchase = LocalDate.of(2026, 1, 1);
        LocalDate expiry = LocalDate.of(2026, 3, 1);

        StockBatch batch = new StockBatch(
                1L,
                new ItemCode("MILK001"),
                purchase,
                expiry,
                new Quantity(10)
        );

        batch.reduceQuantity(new Quantity(4));

        assertEquals(purchase, batch.getPurchaseDate());
        assertEquals(expiry, batch.getExpiryDate());
    }
}
