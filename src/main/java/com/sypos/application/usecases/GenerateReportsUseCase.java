package com.sypos.application.usecases;

import com.sypos.application.dto.reports.*;
import com.sypos.application.ports.BillRepository;
import com.sypos.application.ports.ReportRepository;

import java.time.LocalDate;
import java.util.Objects;

public class GenerateReportsUseCase {

    private final ReportRepository reportRepository;
    private final BillRepository billRepository;

    public GenerateReportsUseCase(ReportRepository reportRepository, BillRepository billRepository) {
        this.reportRepository = Objects.requireNonNull(reportRepository);
        this.billRepository = Objects.requireNonNull(billRepository);
    }

    public DailySalesReport generateDailySales(LocalDate date) {
        return new DailySalesReport(date, reportRepository.fetchDailySales(date));
    }

    public ReshelveReport generateReshelveReport() {
        return new ReshelveReport(reportRepository.fetchReshelveItems());
    }

    public ReorderReport generateReorderReport() {
        // brief: if stock < 50, show on report
        return new ReorderReport(reportRepository.fetchReorderItems(50));
    }

    public StockReport generateStockReport() {
        return new StockReport(reportRepository.fetchStockBatchReport());
    }

    public BillReport generateBillReport(LocalDate date) {
        return new BillReport(billRepository.findByDate(date));
    }
}
