package com.sypos.application.usecases;

import com.sypos.application.dto.reports.*;
import com.sypos.testdoubles.FakeBillRepository;
import com.sypos.testdoubles.FakeReportRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class GenerateReportsUseCaseTest {

    @Test
    void constructorRejectsNullReportRepository() {
        assertThrows(NullPointerException.class,
                () -> new GenerateReportsUseCase(null, new FakeBillRepository()));
    }

    @Test
    void constructorRejectsNullBillRepository() {
        assertThrows(NullPointerException.class,
                () -> new GenerateReportsUseCase(new FakeReportRepository(), null));
    }

    @Test
    void generatesDailySalesReport() {
        FakeReportRepository reportRepo = new FakeReportRepository();
        FakeBillRepository billRepo = new FakeBillRepository();
        GenerateReportsUseCase uc = new GenerateReportsUseCase(reportRepo, billRepo);

        LocalDate date = LocalDate.of(2026, 1, 19);
        DailySalesReport report = uc.generateDailySales(date);

        assertEquals(date, report.getDate());
        assertEquals(1, report.getLines().size());
        assertEquals(date, reportRepo.lastDate);
    }

    @Test
    void generatesReshelveReport() {
        FakeReportRepository reportRepo = new FakeReportRepository();
        GenerateReportsUseCase uc =
                new GenerateReportsUseCase(reportRepo, new FakeBillRepository());

        ReshelveReport report = uc.generateReshelveReport();

        assertEquals(1, report.getLines().size());
    }

    @Test
    void generatesReorderReportWithThreshold50() {
        FakeReportRepository reportRepo = new FakeReportRepository();
        GenerateReportsUseCase uc =
                new GenerateReportsUseCase(reportRepo, new FakeBillRepository());

        ReorderReport report = uc.generateReorderReport();

        assertEquals(1, report.getLines().size());
        assertEquals(50, reportRepo.lastThreshold);
    }

    @Test
    void generatesStockReport() {
        FakeReportRepository reportRepo = new FakeReportRepository();
        GenerateReportsUseCase uc =
                new GenerateReportsUseCase(reportRepo, new FakeBillRepository());

        StockReport report = uc.generateStockReport();

        assertNotNull(report);
        assertTrue(report.getBatches().isEmpty());
    }

    @Test
    void generatesBillReportForDate() {
        FakeBillRepository billRepo = new FakeBillRepository();
        GenerateReportsUseCase uc =
                new GenerateReportsUseCase(new FakeReportRepository(), billRepo);

        LocalDate date = LocalDate.now();
        BillReport report = uc.generateBillReport(date);

        assertNotNull(report);
        assertTrue(report.getBills().isEmpty());
    }
}
