package com.sypos.domain.inventory;

import com.sypos.domain.entities.StockBatch;
import com.sypos.domain.valueobjects.ItemCode;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ExpiryAwareFifoPolicy implements BatchSelectionPolicy {

    @Override
    public StockBatch selectNextBatch(ItemCode itemCode, List<StockBatch> availableBatches) {
        Objects.requireNonNull(itemCode);
        Objects.requireNonNull(availableBatches);

        List<StockBatch> relevant = availableBatches.stream()
                .filter(b -> b.getItemCode().equals(itemCode))
                .filter(b -> b.getQuantity().getValue() > 0)
                .toList();

        if (relevant.isEmpty()) {
            throw new IllegalStateException("No available stock batches for item: " + itemCode);
        }

        StockBatch fifo = relevant.stream()
                .min(Comparator.comparing(StockBatch::getPurchaseDate))
                .orElseThrow();

        StockBatch earliestExpiry = relevant.stream()
                .min(Comparator.comparing(StockBatch::getExpiryDate))
                .orElseThrow();

        if (earliestExpiry.getExpiryDate().isBefore(fifo.getExpiryDate())) {
            return earliestExpiry;
        }
        return fifo;
    }
}