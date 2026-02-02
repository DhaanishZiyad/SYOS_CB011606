package com.sypos.domain.inventory;

import com.sypos.domain.entities.StockBatch;
import com.sypos.domain.valueobjects.ItemCode;
import com.sypos.domain.valueobjects.Quantity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BatchConsumerTest {

    private static StockBatch batch(long id, String code, String purchase, String expiry, int remaining) {
        return new StockBatch(
                id,
                new ItemCode(code),
                LocalDate.parse(purchase),
                LocalDate.parse(expiry),
                new Quantity(remaining)
        );
    }

    @Test
    void consumesFromSingleBatch_whenEnoughQuantity() {
        var policy = new ExpiryAwareFifoPolicy();
        var consumer = new BatchConsumer(policy);

        var milk = new ItemCode("MILK001");
        var batches = new ArrayList<>(List.of(
                batch(1, "MILK001", "2026-01-01", "2026-02-01", 10)
        ));

        consumer.consume(milk, new Quantity(3), batches);

        assertEquals(7, batches.get(0).getQuantity().getValue());
    }

    @Test
    void consumesExactlyToZeroFromSingleBatch() {
        var consumer = new BatchConsumer(new ExpiryAwareFifoPolicy());
        var milk = new ItemCode("MILK001");

        var batches = new ArrayList<>(List.of(
                batch(1, "MILK001", "2026-01-01", "2026-02-01", 5)
        ));

        consumer.consume(milk, new Quantity(5), batches);

        assertEquals(0, batches.get(0).getQuantity().getValue());
    }

    @Test
    void consumesAcrossTwoBatches_whenFirstNotEnough() {
        var consumer = new BatchConsumer(new ExpiryAwareFifoPolicy());
        var milk = new ItemCode("MILK001");

        var batches = new ArrayList<>(List.of(
                batch(1, "MILK001", "2026-01-01", "2026-03-01", 2),
                batch(2, "MILK001", "2026-01-10", "2026-04-01", 10)
        ));

        consumer.consume(milk, new Quantity(5), batches);

        assertEquals(0, batches.get(0).getQuantity().getValue());
        assertEquals(7, batches.get(1).getQuantity().getValue());
    }

    @Test
    void prefersEarlierExpiryBatchEvenIfPurchaseIsNewer() {
        var consumer = new BatchConsumer(new ExpiryAwareFifoPolicy());
        var milk = new ItemCode("MILK001");

        var batches = new ArrayList<>(List.of(
                batch(1, "MILK001", "2026-01-01", "2026-02-01", 100), // FIFO by purchase
                batch(2, "MILK001", "2026-01-10", "2026-01-25", 50)   // earlier expiry -> should be consumed
        ));

        consumer.consume(milk, new Quantity(3), batches);

        // earlier expiry batch should reduce
        assertEquals(47, batches.get(1).getQuantity().getValue());
        assertEquals(100, batches.get(0).getQuantity().getValue());
    }

    @Test
    void consumesOnlyMatchingItemCode_batchesOfOtherItemsUnaffected() {
        var consumer = new BatchConsumer(new ExpiryAwareFifoPolicy());

        var milk = new ItemCode("MILK001");
        var batches = new ArrayList<>(List.of(
                batch(1, "MILK001", "2026-01-01", "2026-02-01", 10),
                batch(2, "BREAD001", "2026-01-01", "2026-01-15", 10)
        ));

        consumer.consume(milk, new Quantity(4), batches);

        assertEquals(6, batches.get(0).getQuantity().getValue());
        assertEquals(10, batches.get(1).getQuantity().getValue());
    }

    @Test
    void throwsWhenInsufficientTotalQuantityAcrossAllBatches_andDoesNotModifyAnything() {
        var consumer = new BatchConsumer(new ExpiryAwareFifoPolicy());
        var milk = new ItemCode("MILK001");

        var batches = new ArrayList<>(List.of(
                batch(1, "MILK001", "2026-01-01", "2026-02-01", 2),
                batch(2, "MILK001", "2026-01-10", "2026-03-01", 2)
        ));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> consumer.consume(milk, new Quantity(10), batches));

        assertTrue(ex.getMessage().contains("Insufficient stock"));

        // ✅ atomic: unchanged
        assertEquals(2, batches.get(0).getQuantity().getValue());
        assertEquals(2, batches.get(1).getQuantity().getValue());
    }


    @Test
    void throwsWhenNoBatchesForItem() {
        var consumer = new BatchConsumer(new ExpiryAwareFifoPolicy());
        var milk = new ItemCode("MILK001");

        var batches = new ArrayList<>(List.of(
                batch(1, "BREAD001", "2026-01-01", "2026-02-01", 10)
        ));

        assertThrows(IllegalStateException.class,
                () -> consumer.consume(milk, new Quantity(1), batches));
    }

    @Test
    void requiredZeroDoesNothingAndReturnsEmptyModifiedList() {
        var consumer = new BatchConsumer(new ExpiryAwareFifoPolicy());
        var milk = new ItemCode("MILK001");

        var batches = new ArrayList<>(List.of(
                batch(1, "MILK001", "2026-01-01", "2026-02-01", 10)
        ));

        var modified = consumer.consume(milk, new Quantity(0), batches);

        assertTrue(modified.isEmpty());
        assertEquals(10, batches.get(0).getQuantity().getValue());
    }

    @Test
    void consumesInMultipleSteps_untilQuantitySatisfied() {
        var consumer = new BatchConsumer(new ExpiryAwareFifoPolicy());
        var milk = new ItemCode("MILK001");

        var batches = new ArrayList<>(List.of(
                batch(1, "MILK001", "2026-01-01", "2026-03-01", 3),
                batch(2, "MILK001", "2026-01-02", "2026-03-02", 3),
                batch(3, "MILK001", "2026-01-03", "2026-03-03", 3)
        ));

        consumer.consume(milk, new Quantity(7), batches);

        assertEquals(0, batches.get(0).getQuantity().getValue());
        assertEquals(0, batches.get(1).getQuantity().getValue());
        assertEquals(2, batches.get(2).getQuantity().getValue());
    }

    @Test
    void leavesUnrelatedBatchesUntouched_whenConsumingFromEarlyExpiry() {
        var consumer = new BatchConsumer(new ExpiryAwareFifoPolicy());
        var milk = new ItemCode("MILK001");

        var batches = new ArrayList<>(List.of(
                batch(1, "MILK001", "2026-01-01", "2026-02-01", 10),
                batch(2, "MILK001", "2026-01-10", "2026-01-25", 10),
                batch(3, "BREAD001", "2026-01-01", "2026-01-20", 10)
        ));

        consumer.consume(milk, new Quantity(2), batches);

        // earliest expiry among milk is batch 2 => should be reduced
        assertEquals(8, batches.get(1).getQuantity().getValue());
        assertEquals(10, batches.get(0).getQuantity().getValue());
        assertEquals(10, batches.get(2).getQuantity().getValue());
    }
}
