package com.sypos.application.ports;

import com.sypos.application.dto.reports.ReorderLine;
import com.sypos.application.dto.reports.ReshelveLine;
import com.sypos.application.dto.reports.SalesLine;
import com.sypos.application.dto.reports.StockBatchLine;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository {

    List<SalesLine> fetchDailySales(LocalDate date);

    List<ReshelveLine> fetchReshelveItems();

    List<ReorderLine> fetchReorderItems(int threshold);

    List<StockBatchLine> fetchStockBatchReport();

}
