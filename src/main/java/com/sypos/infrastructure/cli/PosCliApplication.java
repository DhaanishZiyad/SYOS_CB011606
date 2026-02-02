package com.sypos.infrastructure.cli;

import com.sypos.adapters.controllers.PosController;
import com.sypos.adapters.presenters.ConsoleBillPresenter;
import com.sypos.adapters.repositories.JdbcBillRepository;
import com.sypos.adapters.repositories.JdbcInventoryRepository;
import com.sypos.adapters.repositories.JdbcItemRepository;
import com.sypos.adapters.repositories.JdbcReportRepository;
import com.sypos.application.usecases.AddItemToBillUseCase;
import com.sypos.application.usecases.CreateBillUseCase;
import com.sypos.application.usecases.FinalizeCheckoutUseCase;
import com.sypos.application.usecases.GenerateReportsUseCase;
import com.sypos.domain.inventory.BatchConsumer;
import com.sypos.domain.inventory.ExpiryAwareFifoPolicy;
import com.sypos.domain.payment.CashPaymentStrategy;
import com.sypos.infrastructure.mysql.MySqlConnectionFactory;
import com.sypos.infrastructure.pdf.PdfReportExporter;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Scanner;
import java.nio.file.Path;

public class PosCliApplication {

    public static void main(String[] args) {
        // --- DB Config (change if your port/user/pass differ)
        String url = "jdbc:mysql://localhost:3306/syos_pos?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String pass = "";

        var factory = new MySqlConnectionFactory(url, user, pass);

        // --- Repositories
        var itemRepo = new JdbcItemRepository(factory);
        var billRepo = new JdbcBillRepository(factory);
        var invRepo = new JdbcInventoryRepository(factory);
        var reportRepo = new JdbcReportRepository(factory, 100); // target shelf level = 100

        // --- Use cases
        var createBillUC = new CreateBillUseCase(billRepo, Clock.systemDefaultZone());
        var addItemUC = new AddItemToBillUseCase(itemRepo);

        var batchConsumer = new BatchConsumer(new ExpiryAwareFifoPolicy());
        var paymentStrategy = new CashPaymentStrategy();

        var finalizeUC = new FinalizeCheckoutUseCase(invRepo, billRepo, batchConsumer, paymentStrategy);
        var reportsUC = new GenerateReportsUseCase(reportRepo, billRepo);
        var exporter = new PdfReportExporter(Path.of("reports"));

        // --- Controller + Presenter
        var controller = new PosController(createBillUC, addItemUC, finalizeUC, reportsUC, exporter);
        var presenter = new ConsoleBillPresenter();

        // --- CLI Loop
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== SYOS POS (CLI) ===");
            System.out.println("1) New Sale");
            System.out.println("2) Reports");
            System.out.println("0) Exit");
            System.out.print("Select: ");
            String choice = sc.nextLine().trim();

            if (choice.equals("0")) {
                System.out.println("Goodbye!");
                break;
            }

            switch (choice) {
                case "1" -> runSale(sc, controller, presenter);
                case "2" -> runReports(sc, controller);
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void runSale(Scanner sc, PosController controller, ConsoleBillPresenter presenter) {
        var bill = controller.startNewSale();
        System.out.println("\nNew sale started. Bill serial: " + bill.getSerialNumber());

        while (true) {
            System.out.println("\n--- Sale Menu ---");
            System.out.println("1) Add item");
            System.out.println("2) View bill");
            System.out.println("3) Checkout");
            System.out.println("0) Cancel sale");
            System.out.print("Select: ");
            String ch = sc.nextLine().trim();

            switch (ch) {
                case "1" -> {
                    System.out.print("Enter item code: ");
                    String code = sc.nextLine().trim();
                    System.out.print("Enter quantity: ");
                    int qty = Integer.parseInt(sc.nextLine().trim());

                    try {
                        controller.addItem(bill, code, qty);
                        System.out.println("✅ Added.");
                    } catch (Exception e) {
                        System.out.println("❌ " + e.getMessage());
                    }
                }
                case "2" -> presenter.printBill(bill);

                case "3" -> {
                    presenter.printBill(bill);
                    System.out.print("Cash tendered: ");
                    BigDecimal tendered = new BigDecimal(sc.nextLine().trim());

                    try {
                        var result = controller.checkout(bill, tendered);
                        presenter.printPayment(result);
                        System.out.println("✅ Sale completed and saved.");
                        return;
                    } catch (Exception e) {
                        System.out.println("❌ Checkout failed: " + e.getMessage());
                    }
                }

                case "0" -> {
                    System.out.println("Sale cancelled.");
                    return;
                }

                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void runReports(Scanner sc, PosController controller) {
        while (true) {
            System.out.println("\n--- Reports Menu ---");
            System.out.println("1) Daily sales (today)");
            System.out.println("2) Reorder report");
            System.out.println("3) Reshelve report");
            System.out.println("4) Stock batch report");
            System.out.println("5) Bill report (today)");
            System.out.println("6) Export daily sales (today) PDF");
            System.out.println("7) Export reorder report PDF");
            System.out.println("8) Export reshelve report PDF");
            System.out.println("9) Export stock batch report PDF");
            System.out.println("10) Export bill report (today) PDF");
            System.out.println("0) Back");
            System.out.print("Select: ");
            String ch = sc.nextLine().trim();

            switch (ch) {
                case "1" -> controller.showDailySales(LocalDate.now());
                case "2" -> controller.showReorder();
                case "3" -> controller.showReshelve();
                case "4" -> controller.showStockBatches();
                case "5" -> controller.showBillReport(LocalDate.now());
                case "6" -> {
                    var saved = controller.exportDailySalesPdf(LocalDate.now());
                    System.out.println("✅ Saved PDF: " + saved.toAbsolutePath());
                }
                case "7" -> {
                    var saved = controller.exportReorderPdf();
                    System.out.println("✅ Saved PDF: " + saved.toAbsolutePath());
                }
                case "8" -> {
                    var saved = controller.exportReshelvePdf();
                    System.out.println("✅ Saved PDF: " + saved.toAbsolutePath());
                }
                case "9" -> {
                    var saved = controller.exportStockBatchPdf();
                    System.out.println("✅ Saved PDF: " + saved.toAbsolutePath());
                }
                case "10" -> {
                    var saved = controller.exportBillReportPdf(LocalDate.now());
                    System.out.println("✅ Saved PDF: " + saved.toAbsolutePath());
                }
                case "0" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}
