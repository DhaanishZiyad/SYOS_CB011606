package com.sypos.domain.inventory;

import com.sypos.domain.entities.StockBatch;
import com.sypos.domain.valueobjects.ItemCode;
import com.sypos.domain.valueobjects.Quantity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpiryAwareFifoPolicyTest {

    private final ExpiryAwareFifoPolicy policy = new ExpiryAwareFifoPolicy();

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
    void throwsWhenNoBatchesProvided() {
        assertThrows(IllegalStateException.class,
                () -> policy.selectNextBatch(new ItemCode("MILK001"), List.of()));
    }

    @Test
    void throwsWhenNoBatchesMatchItemCode() {
        StockBatch otherItem = batch(1, "BREAD001", "2026-01-01", "2026-02-01", 10);

        assertThrows(IllegalStateException.class,
                () -> policy.selectNextBatch(new ItemCode("MILK001"), List.of(otherItem)));
    }

    @Test
    void throwsWhenAllMatchingBatchesHaveZeroOrLessQuantity() {
        // Quantity can't be 0 with your Quantity VO, so simulate "not available" by using a different item
        // OR if you ever allow Quantity(0), test would use that. Here we test with no valid (filtered) batches:
        StockBatch otherItem = batch(1, "BREAD001", "2026-01-01", "2026-02-01", 10);

        assertThrows(IllegalStateException.class,
                () -> policy.selectNextBatch(new ItemCode("MILK001"), List.of(otherItem)));
    }

    @Test
    void whenOnlyOneRelevantBatch_returnsIt() {
        StockBatch b1 = batch(1, "MILK001", "2026-01-01", "2026-02-01", 10);

        StockBatch selected = policy.selectNextBatch(new ItemCode("MILK001"), List.of(b1));

        assertEquals(1L, selected.getId());
    }

    @Test
    void fifoSelectedByOldestPurchaseDateWhenExpiryRuleNotTriggered() {
        StockBatch fifo = batch(1, "MILK001", "2026-01-01", "2026-02-01", 10);
        StockBatch newer = batch(2, "MILK001", "2026-01-10", "2026-03-01", 10);

        StockBatch selected = policy.selectNextBatch(new ItemCode("MILK001"), List.of(newer, fifo));

        assertEquals(1L, selected.getId());
    }

    @Test
    void selectsEarlierExpiryWhenItIsBeforeFifoExpiryEvenIfPurchaseIsNewer() {
        StockBatch fifo = batch(1, "MILK001", "2026-01-01", "2026-02-01", 100);
        StockBatch newerEarlierExpiry = batch(2, "MILK001", "2026-01-10", "2026-01-25", 50);

        StockBatch selected = policy.selectNextBatch(new ItemCode("MILK001"), List.of(fifo, newerEarlierExpiry));

        assertEquals(2L, selected.getId());
    }

    @Test
    void doesNotSelectEarlierExpiryIfItIsNotBeforeFifoExpiry_equalExpiryMeansFifo() {
        StockBatch fifo = batch(1, "MILK001", "2026-01-01", "2026-02-01", 10);
        StockBatch sameExpiryNewerPurchase = batch(2, "MILK001", "2026-01-10", "2026-02-01", 10);

        StockBatch selected = policy.selectNextBatch(new ItemCode("MILK001"), List.of(fifo, sameExpiryNewerPurchase));

        assertEquals(1L, selected.getId());
    }

    @Test
    void doesNotSelectEarlierExpiryIfItIsAfterFifoExpiry() {
        StockBatch fifo = batch(1, "MILK001", "2026-01-01", "2026-01-20", 10);
        StockBatch laterExpiry = batch(2, "MILK001", "2026-01-10", "2026-02-01", 10);

        StockBatch selected = policy.selectNextBatch(new ItemCode("MILK001"), List.of(fifo, laterExpiry));

        assertEquals(1L, selected.getId());
    }

    @Test
    void ignoresBatchesOfOtherItems() {
        StockBatch milk = batch(1, "MILK001", "2026-01-01", "2026-02-01", 10);
        StockBatch breadEarlierExpiry = batch(2, "BREAD001", "2026-01-01", "2026-01-10", 10);

        StockBatch selected = policy.selectNextBatch(new ItemCode("MILK001"), List.of(milk, breadEarlierExpiry));

        assertEquals(1L, selected.getId());
    }

    @Test
    void whenMultipleBatches_exist_fifoCandidateFoundThenExpiryRuleMayOverride() {
        StockBatch fifo = batch(1, "MILK001", "2026-01-01", "2026-03-01", 10);
        StockBatch mid = batch(2, "MILK001", "2026-01-05", "2026-02-15", 10);
        StockBatch newestEarliestExpiry = batch(3, "MILK001", "2026-01-10", "2026-01-20", 10);

        StockBatch selected = policy.selectNextBatch(new ItemCode("MILK001"), List.of(mid, fifo, newestEarliestExpiry));

        // fifo expiry is 2026-03-01, earliest expiry is 2026-01-20 => override
        assertEquals(3L, selected.getId());
    }
}
