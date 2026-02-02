package com.sypos.application.dto.reports;

import java.util.List;

public final class StockReport {
    private final List<StockBatchLine> batches;

    public StockReport(List<StockBatchLine> batches) {
        this.batches = List.copyOf(batches);
    }

    public List<StockBatchLine> getBatches() { return batches; }
}
