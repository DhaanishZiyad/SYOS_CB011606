package com.sypos.infrastructure.pdf;

import com.sypos.application.dto.reports.*;
import com.sypos.application.ports.ReportExporter;

import java.nio.file.Path;
import java.time.LocalDate;

public class DummyReportExporter implements ReportExporter {

    @Override
    public Path exportDailySales(DailySalesReport report) {
        System.out.println("Dummy: exportDailySales called");
        return null;
    }

    @Override
    public Path exportReorder(ReorderReport report) {
        System.out.println("Dummy: exportReorder called");
        return null;
    }

    @Override
    public Path exportReshelve(ReshelveReport report) {
        System.out.println("Dummy: exportReshelve called");
        return null;
    }

    @Override
    public Path exportStock(StockReport report) {
        System.out.println("Dummy: exportStock called");
        return null;
    }

    @Override
    public Path exportBillReport(LocalDate date, BillReport report) {
        System.out.println("Dummy: exportBillReport called");
        return null;
    }
}