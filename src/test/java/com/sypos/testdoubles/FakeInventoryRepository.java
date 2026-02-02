package com.sypos.testdoubles;

import com.sypos.application.ports.InventoryRepository;
import com.sypos.domain.entities.ShelfStock;
import com.sypos.domain.entities.StockBatch;
import com.sypos.domain.valueobjects.ItemCode;

import java.util.*;

public class FakeInventoryRepository implements InventoryRepository {

    private final Map<String, ShelfStock> shelf = new HashMap<>();
    private final Map<String, List<StockBatch>> batches = new HashMap<>();

    public int saveShelfCalls = 0;
    public int saveBatchesCalls = 0;

    public void putShelf(ShelfStock shelfStock) {
        shelf.put(shelfStock.getItemCode().getValue(), shelfStock);
    }

    public void putBatches(ItemCode code, List<StockBatch> list) {
        batches.put(code.getValue(), new ArrayList<>(list));
    }

    @Override
    public Optional<ShelfStock> findShelfStock(ItemCode code) {
        return Optional.ofNullable(shelf.get(code.getValue()));
    }

    @Override
    public void saveShelfStock(ShelfStock shelfStock) {
        saveShelfCalls++;
        shelf.put(shelfStock.getItemCode().getValue(), shelfStock);
    }

    @Override
    public List<StockBatch> findAvailableBatches(ItemCode code) {
        return batches.getOrDefault(code.getValue(), List.of());
    }

    @Override
    public void saveBatches(List<StockBatch> modifiedBatches) {
        saveBatchesCalls++;
        // objects are already mutated in-memory; this mimics persistence
    }

    public ShelfStock getShelf(ItemCode code) {
        return shelf.get(code.getValue());
    }
}
