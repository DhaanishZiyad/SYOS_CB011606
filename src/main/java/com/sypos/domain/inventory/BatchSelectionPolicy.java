package com.sypos.domain.inventory;

import com.sypos.domain.entities.StockBatch;
import com.sypos.domain.valueobjects.ItemCode;

import java.util.List;

public interface BatchSelectionPolicy {
    StockBatch selectNextBatch(ItemCode itemCode, List<StockBatch> availableBatches);
}