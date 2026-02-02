package com.sypos.application.ports;

import com.sypos.domain.entities.ShelfStock;
import com.sypos.domain.entities.StockBatch;
import com.sypos.domain.valueobjects.ItemCode;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository {

    Optional<ShelfStock> findShelfStock(ItemCode itemCode);

    void saveShelfStock(ShelfStock shelfStock);

    List<StockBatch> findAvailableBatches(ItemCode itemCode);

    void saveBatches(List<StockBatch> modifiedBatches);
}
