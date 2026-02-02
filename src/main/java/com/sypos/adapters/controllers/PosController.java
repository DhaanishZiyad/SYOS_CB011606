package com.sypos.adapters.controllers;

import com.sypos.application.dto.CheckoutResult;
import com.sypos.application.usecases.AddItemToBillUseCase;
import com.sypos.application.usecases.CreateBillUseCase;
import com.sypos.application.usecases.FinalizeCheckoutUseCase;
import com.sypos.application.usecases.GenerateReportsUseCase;
import com.sypos.application.ports.ReportExporter;
import com.sypos.domain.entities.Bill;
import com.sypos.domain.valueobjects.ItemCode;
import com.sypos.domain.valueobjects.Money;
import com.sypos.domain.valueobjects.Quantity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.nio.file.Path;


public class PosController {

    private final CreateBillUseCase createBillUseCase;
    private final AddItemToBillUseCase addItemToBillUseCase;
    private final FinalizeCheckoutUseCase finalizeCheckoutUseCase;
    private final GenerateReportsUseCase generateReportsUseCase;
    private final ReportExporter reportExporter;


    public PosController(
            CreateBillUseCase createBillUseCase,
            AddItemToBillUseCase addItemToBillUseCase,
            FinalizeCheckoutUseCase finalizeCheckoutUseCase,
            GenerateReportsUseCase generateReportsUseCase,
            ReportExporter reportExporter
    ) {
        this.createBillUseCase = Objects.requireNonNull(createBillUseCase);
        this.addItemToBillUseCase = Objects.requireNonNull(addItemToBillUseCase);
        this.finalizeCheckoutUseCase = Objects.requireNonNull(finalizeCheckoutUseCase);
        this.generateReportsUseCase = Objects.requireNonNull(generateReportsUseCase);
        this.reportExporter = Objects.requireNonNull(reportExporter);
    }

    public Bill startNewSale() {
        return createBillUseCase.createNewBill();
    }

    public void addItem(Bill bill, String code, int qty) {
        addItemToBillUseCase.addItem(bill, new ItemCode(code), new Quantity(qty));
    }

    public CheckoutResult checkout(Bill bill, BigDecimal tendered) {
        return finalizeCheckoutUseCase.finalizeSale(bill, new Money(tendered));
    }

    public void showDailySales(LocalDate date) {
        var report = generateReportsUseCase.generateDailySales(date);
        System.out.println("Daily Sales for: " + report.getDate());
        report.getLines().forEach(l ->
                System.out.println(l.getItemCode() + " " + l.getItemName() +
                        " qty=" + l.getTotalQuantity() +
                        " revenue=" + l.getTotalRevenue().getAmount())
        );
    }

    public void showReorder() {
        var report = generateReportsUseCase.generateReorderReport();
        System.out.println("Reorder (<50 store stock)");
        report.getLines().forEach(l ->
                System.out.println(l.getItemCode() + " " + l.getItemName() +
                        " stock=" + l.getCurrentStock())
        );
    }

    public void showReshelve() {
        var report = generateReportsUseCase.generateReshelveReport();
        System.out.println("Reshelve (to target shelf level)");
        report.getLines().forEach(l ->
                System.out.println(l.getItemCode() + " " + l.getItemName() +
                        " reshelve=" + l.getQuantityToReshelve())
        );
    }

    public void showStockBatches() {
        var report = generateReportsUseCase.generateStockReport();
        System.out.println("Stock Batch Report");
        report.getBatches().forEach(b ->
                System.out.println(b.getItemCode() +
                        " purchase=" + b.getPurchaseDate() +
                        " expiry=" + b.getExpiryDate() +
                        " received=" + b.getReceivedQuantity() +
                        " remaining=" + b.getRemainingQuantity())
        );
    }

    public void showBillReport(LocalDate date) {
        var report = generateReportsUseCase.generateBillReport(date);

        System.out.println("Bill Report for: " + date);
        if (report.getBills().isEmpty()) {
            System.out.println("(no bills)");
            return;
        }

        for (var b : report.getBills()) {
            System.out.println("Serial=" + b.getSerialNumber()
                    + " total=" + b.getFinalTotal().getAmount()
                    + " tendered=" + b.getCashTendered().getAmount()
                    + " change=" + b.getChangeAmount().getAmount());
        }
    }

    public Path exportDailySalesPdf(LocalDate date) {
        var report = generateReportsUseCase.generateDailySales(date);
        return reportExporter.exportDailySales(report);
    }

    public Path exportReorderPdf() {
        var report = generateReportsUseCase.generateReorderReport();
        return reportExporter.exportReorder(report);
    }

    public Path exportReshelvePdf() {
        var report = generateReportsUseCase.generateReshelveReport();
        return reportExporter.exportReshelve(report);
    }

    public Path exportStockBatchPdf() {
        var report = generateReportsUseCase.generateStockReport();
        return reportExporter.exportStock(report);
    }

    public Path exportBillReportPdf(LocalDate date) {
        var report = generateReportsUseCase.generateBillReport(date);
        return reportExporter.exportBillReport(date, report);
    }
}
