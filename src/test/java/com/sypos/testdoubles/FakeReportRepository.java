package com.sypos.testdoubles;

import com.sypos.application.dto.reports.*;
import com.sypos.application.ports.ReportRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class FakeReportRepository implements ReportRepository {

    public LocalDate lastDate;
    public Integer lastThreshold;

    @Override
    public List<SalesLine> fetchDailySales(LocalDate date) {
        this.lastDate = date;
        return List.of(new SalesLine("MILK001", "Milk", 2, null));
    }

    @Override
    public List<ReshelveLine> fetchReshelveItems() {
        return List.of(new ReshelveLine("MILK001", "Milk", 5));
    }

    @Override
    public List<ReorderLine> fetchReorderItems(int threshold) {
        this.lastThreshold = threshold;
        return List.of(new ReorderLine("MILK001", "Milk", 40));
    }

    @Override
    public List<StockBatchLine> fetchStockBatchReport() {
        return Collections.emptyList();
    }
}
