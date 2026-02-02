package com.sypos.infrastructure.pdf;

import com.sypos.application.dto.reports.*;
import com.sypos.domain.entities.Bill;
import com.sypos.domain.valueobjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PdfReportExporterTest {

    private PdfReportExporter exporter(Path dir) {
        return new PdfReportExporter(dir);
    }

    @Test
    void exportsDailySalesReport() throws Exception {
        Path dir = Files.createTempDirectory("pdf-test");
        PdfReportExporter exporter = exporter(dir);

        DailySalesReport report =
                new DailySalesReport(LocalDate.now(), List.of());

        Path out = exporter.exportDailySales(report);

        assertTrue(Files.exists(out));
    }

    @Test
    void exportsReorderReport() throws Exception {
        Path dir = Files.createTempDirectory("pdf-test");
        PdfReportExporter exporter = exporter(dir);

        ReorderReport report =
                new ReorderReport(List.of());

        Path out = exporter.exportReorder(report);

        assertTrue(Files.exists(out));
    }

    @Test
    void exportsReshelveReport() throws Exception {
        Path dir = Files.createTempDirectory("pdf-test");
        PdfReportExporter exporter = exporter(dir);

        ReshelveReport report =
                new ReshelveReport(List.of());

        Path out = exporter.exportReshelve(report);

        assertTrue(Files.exists(out));
    }

    @Test
    void exportsStockReport() throws Exception {
        Path dir = Files.createTempDirectory("pdf-test");
        PdfReportExporter exporter = exporter(dir);

        StockReport report =
                new StockReport(List.of());

        Path out = exporter.exportStock(report);

        assertTrue(Files.exists(out));
    }

    @Test
    void exportsBillReport() throws Exception {
        Path dir = Files.createTempDirectory("pdf-test");
        PdfReportExporter exporter = exporter(dir);

        Bill bill = new Bill(1, LocalDate.now());
        bill.recordPayment(
                new Money(new BigDecimal("100")),
                new Money(new BigDecimal("100"))
        );

        BillReport report =
                new BillReport(List.of(bill));

        Path out = exporter.exportBillReport(LocalDate.now(), report);

        assertTrue(Files.exists(out));
    }
}
