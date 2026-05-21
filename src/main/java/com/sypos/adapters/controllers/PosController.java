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
import com.sypos.application.dto.reports.BillReport;
import com.sypos.application.ports.BillRepository;
import com.sypos.application.ports.ItemRepository;
import com.sypos.application.ports.InventoryRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Map;
import java.util.List;
import java.nio.file.Path;
import com.sypos.domain.entities.Item;
import com.sypos.domain.entities.StockBatch;


public class PosController {

    private final CreateBillUseCase createBillUseCase;
    private final AddItemToBillUseCase addItemToBillUseCase;
    private final FinalizeCheckoutUseCase finalizeCheckoutUseCase;
    private final GenerateReportsUseCase generateReportsUseCase;
    private final ReportExporter reportExporter;
    private final BillRepository billRepository;
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;


    public PosController(
            CreateBillUseCase createBillUseCase,
            AddItemToBillUseCase addItemToBillUseCase,
            FinalizeCheckoutUseCase finalizeCheckoutUseCase,
            GenerateReportsUseCase generateReportsUseCase,
            ReportExporter reportExporter,
            BillRepository billRepository,
            ItemRepository itemRepository,
            InventoryRepository inventoryRepository
    ) {
        this.createBillUseCase = Objects.requireNonNull(createBillUseCase);
        this.addItemToBillUseCase = Objects.requireNonNull(addItemToBillUseCase);
        this.finalizeCheckoutUseCase = Objects.requireNonNull(finalizeCheckoutUseCase);
        this.generateReportsUseCase = Objects.requireNonNull(generateReportsUseCase);
        this.reportExporter = Objects.requireNonNull(reportExporter);
        this.billRepository = billRepository;
        this.itemRepository = itemRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public Bill startNewSale() {
        return createBillUseCase.createNewBill();
    }

    public void addItem(Bill bill, String code, int qty) {
        addItemToBillUseCase.addItem(bill, new ItemCode(code), new Quantity(qty));
    }

    public void removeItem(Bill bill, int index) {
        bill.removeItem(index);
    }

    public CheckoutResult checkout(Bill bill, BigDecimal tendered) {
        return finalizeCheckoutUseCase.finalizeSale(bill, new Money(tendered));
    }

    public void createItem(
            String code,
            String name,
            java.math.BigDecimal unitPrice
    ) {

        Item item =
                new Item(
                        new ItemCode(code),
                        name,
                        new Money(unitPrice)
                );

        itemRepository.save(item);
    }

    public void restockItem(
            String itemCode,
            int quantity,
            LocalDate purchaseDate,
            LocalDate expiryDate
    ) {

        var code =
                new com.sypos.domain.valueobjects.ItemCode(
                        itemCode
                );

        // --- Update shelf stock

        var shelfStock =
                inventoryRepository
                        .findShelfStock(code)
                        .orElse(
                                new com.sypos.domain.entities.ShelfStock(
                                        code,
                                        new com.sypos.domain.valueobjects.Quantity(0)
                                )
                        );

        int updatedQty =
                shelfStock.getQuantity().getValue()
                        + quantity;

        var updatedStock =
                new com.sypos.domain.entities.ShelfStock(
                        code,
                        new com.sypos.domain.valueobjects.Quantity(updatedQty)
                );

        inventoryRepository.saveShelfStock(updatedStock);

        // --- Create stock batch

        var batch =
                new com.sypos.domain.entities.StockBatch(
                        0,
                        code,
                        purchaseDate,
                        expiryDate,
                        new com.sypos.domain.valueobjects.Quantity(quantity)
                );

        inventoryRepository.saveNewBatch(batch);
    }

    public void removeBatch(
            long batchId,
            String itemCode,
            int remainingQty
    ) {

        var code =
                new com.sypos.domain.valueobjects.ItemCode(
                        itemCode
                );

        // --- Update shelf stock

        var shelfStock =
                inventoryRepository
                        .findShelfStock(code)
                        .orElseThrow();

        int updatedQty =
                shelfStock.getQuantity().getValue()
                        - remainingQty;

        if (updatedQty < 0) {
            updatedQty = 0;
        }

        inventoryRepository.saveShelfStock(
                new com.sypos.domain.entities.ShelfStock(
                        code,
                        new com.sypos.domain.valueobjects.Quantity(
                                updatedQty
                        )
                )
        );

        // --- Delete batch

        inventoryRepository.deleteBatch(batchId);
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
        var report = generateReportsUseCase.generateBillReport();

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

    public BillReport generateBillReport() {
        return generateReportsUseCase.generateBillReport();
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
        var report = generateReportsUseCase.generateBillReport();
        return reportExporter.exportBillReport(date, report);
    }

    public Bill findBillBySerial(int serial) {
        return billRepository.findBySerial(serial)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public List<StockBatch> getBatchesForItem(String itemCode) {
        return inventoryRepository.findAvailableBatches(
                new ItemCode(itemCode)
        );
    }

    public Map<String, Integer> getAllShelfStock() {
        return inventoryRepository.getAllShelfStock();
    }


}
