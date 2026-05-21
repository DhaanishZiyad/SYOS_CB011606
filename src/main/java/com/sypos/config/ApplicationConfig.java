package com.sypos.config;

import com.sypos.adapters.controllers.PosController;
import com.sypos.adapters.repositories.*;
import com.sypos.application.usecases.*;
import com.sypos.domain.inventory.*;
import com.sypos.domain.payment.*;
import com.sypos.infrastructure.mysql.MySqlConnectionFactory;
import com.sypos.infrastructure.pdf.PdfReportExporter;
import com.sypos.application.ports.ReportExporter;
import com.sypos.infrastructure.pdf.DummyReportExporter;
import com.sypos.concurrency.CheckoutWorker;
import java.nio.file.Path;


import java.time.Clock;

public class ApplicationConfig {

    public static PosController createController() {

        // DB connection
        var connectionFactory = new MySqlConnectionFactory(
                "jdbc:mysql://localhost:3306/syos_pos?useSSL=false&serverTimezone=UTC",
                "root",
                ""
        );

        // Repositories
        var billRepo = new JdbcBillRepository(connectionFactory);
        var inventoryRepo = new JdbcInventoryRepository(connectionFactory);
        var itemRepo = new JdbcItemRepository(connectionFactory);
        var reportRepo = new JdbcReportRepository(connectionFactory, 50);

        // Domain services
        var batchPolicy = new ExpiryAwareFifoPolicy();
        var batchConsumer = new BatchConsumer(batchPolicy);

        var paymentStrategy = new CashPaymentStrategy();

        // Use cases
        var createBillUseCase = new CreateBillUseCase(billRepo, Clock.systemDefaultZone());
        var addItemUseCase = new AddItemToBillUseCase(itemRepo);
        var checkoutUseCase = new FinalizeCheckoutUseCase(
                inventoryRepo,
                billRepo,
                batchConsumer,
                paymentStrategy
        );
        var generateReportsUseCase = new GenerateReportsUseCase(reportRepo, billRepo);

        // Exporter
        ReportExporter reportExporter = new DummyReportExporter();

        // Controller
        PosController controller = new PosController(
                createBillUseCase,
                addItemUseCase,
                checkoutUseCase,
                generateReportsUseCase,
                reportExporter,
                billRepo,
                itemRepo,
                inventoryRepo
        );

        for (int i = 1; i <= 3; i++) {

            Thread worker = new Thread(
                    new CheckoutWorker(controller)
            );

            worker.setName("Checkout-Worker-" + i);

            worker.setDaemon(true);
            worker.start();
        }

        return controller;
    }
}