package com.sypos.application.ports;

import com.sypos.application.dto.reports.*;

import java.nio.file.Path;
import java.time.LocalDate;

public interface ReportExporter {
    Path exportDailySales(DailySalesReport report);
    Path exportReorder(ReorderReport report);
    Path exportReshelve(ReshelveReport report);
    Path exportStock(StockReport report);
    Path exportBillReport(LocalDate date, BillReport report);
}
