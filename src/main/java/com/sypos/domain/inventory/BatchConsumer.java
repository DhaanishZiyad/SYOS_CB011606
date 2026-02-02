package com.sypos.domain.inventory;

import com.sypos.domain.entities.StockBatch;
import com.sypos.domain.valueobjects.ItemCode;
import com.sypos.domain.valueobjects.Quantity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BatchConsumer {

    private final BatchSelectionPolicy selectionPolicy;

    public BatchConsumer(BatchSelectionPolicy selectionPolicy) {
        this.selectionPolicy = Objects.requireNonNull(selectionPolicy);
    }

    public List<StockBatch> consume(ItemCode itemCode, Quantity required, List<StockBatch> batches) {
        Objects.requireNonNull(itemCode);
        Objects.requireNonNull(required);
        Objects.requireNonNull(batches);

        int requiredQty = required.getValue();

        // If nothing required, do nothing
        if (requiredQty == 0) {
            return List.of();
        }

        // ✅ Pre-check total availability (atomic behavior)
        int totalAvailable = batches.stream()
                .filter(b -> b.getItemCode().equals(itemCode))
                .mapToInt(b -> b.getQuantity().getValue())
                .sum();

        if (totalAvailable < requiredQty) {
            throw new IllegalStateException(
                    "Insufficient stock for item: " + itemCode +
                            " (required=" + requiredQty + ", available=" + totalAvailable + ")"
            );
        }

        int remaining = requiredQty;
        List<StockBatch> modified = new ArrayList<>();

        while (remaining > 0) {
            StockBatch next = selectionPolicy.selectNextBatch(itemCode, batches);

            int available = next.getQuantity().getValue();
            int take = Math.min(available, remaining);

            next.reduceQuantity(new Quantity(take));
            modified.add(next);

            remaining -= take;
        }

        return modified;
    }

}
