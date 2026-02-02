package com.sypos.infrastructure.pdf;

import com.sypos.application.dto.reports.*;
import com.sypos.application.ports.ReportExporter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Objects;

public class PdfReportExporter implements ReportExporter {

    private final Path outputDir;

    public PdfReportExporter(Path outputDir) {
        this.outputDir = Objects.requireNonNull(outputDir);
    }

    @Override
    public Path exportDailySales(DailySalesReport report) {
        try {
            Files.createDirectories(outputDir);

            String fileName = "daily_sales_" + report.getDate() + ".pdf";
            Path out = outputDir.resolve(fileName);

            try (PDDocument doc = new PDDocument()) {
                PDPage page = new PDPage();
                doc.addPage(page);

                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                    cs.newLineAtOffset(50, 750);
                    cs.showText("SYOS - Daily Sales Report");
                    cs.endText();

                    int y = 720;

                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 12);
                    cs.newLineAtOffset(50, y);
                    cs.showText("Date: " + report.getDate());
                    cs.endText();

                    y -= 25;

                    // Header
                    writeLine(cs, 50, y, "CODE     ITEM                 QTY     REVENUE");
                    y -= 15;
                    writeLine(cs, 50, y, "------------------------------------------------------");
                    y -= 15;

                    for (SalesLine line : report.getLines()) {
                        String row = pad(line.getItemCode(), 8) + " "
                                + pad(line.getItemName(), 20) + " "
                                + pad(String.valueOf(line.getTotalQuantity()), 6) + " "
                                + line.getTotalRevenue().getAmount();
                        writeLine(cs, 50, y, row);
                        y -= 15;

                        // Simple page overflow handling
                        if (y < 60) break;
                    }
                }

                doc.save(out.toFile());
            }

            return out;

        } catch (Exception e) {
            throw new RuntimeException("Failed to export Daily Sales PDF", e);
        }
    }

    @Override
    public Path exportReorder(ReorderReport report) {
        try {
            Files.createDirectories(outputDir);

            String fileName = "reorder_report_" + LocalDate.now() + ".pdf";
            Path out = outputDir.resolve(fileName);

            try (PDDocument doc = new PDDocument()) {
                PDPage page = new PDPage();
                doc.addPage(page);

                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {

                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                    cs.newLineAtOffset(50, 750);
                    cs.showText("SYOS - Reorder Report");
                    cs.endText();

                    int y = 720;

                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 12);
                    cs.newLineAtOffset(50, y);
                    cs.showText("Generated: " + LocalDate.now());
                    cs.endText();

                    y -= 25;

                    writeLine(cs, 50, y, "CODE     ITEM                 STORE_STOCK");
                    y -= 15;
                    writeLine(cs, 50, y, "------------------------------------------------------");
                    y -= 15;

                    for (ReorderLine line : report.getLines()) {
                        String row = pad(line.getItemCode(), 8) + " "
                                + pad(line.getItemName(), 20) + " "
                                + line.getCurrentStock();
                        writeLine(cs, 50, y, row);
                        y -= 15;

                        if (y < 60) break;
                    }
                }

                doc.save(out.toFile());
            }

            return out;

        } catch (Exception e) {
            throw new RuntimeException("Failed to export Reorder Report PDF", e);
        }
    }

    @Override
    public Path exportReshelve(ReshelveReport report) {
        try {
            Files.createDirectories(outputDir);

            String fileName = "reshelve_report_" + LocalDate.now() + ".pdf";
            Path out = outputDir.resolve(fileName);

            try (PDDocument doc = new PDDocument()) {
                PDPage page = new PDPage();
                doc.addPage(page);

                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {

                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                    cs.newLineAtOffset(50, 750);
                    cs.showText("SYOS - Reshelve Report");
                    cs.endText();

                    int y = 720;

                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 12);
                    cs.newLineAtOffset(50, y);
                    cs.showText("Generated: " + LocalDate.now());
                    cs.endText();

                    y -= 25;

                    writeLine(cs, 50, y, "CODE     ITEM                 TO_RESHELVE");
                    y -= 15;
                    writeLine(cs, 50, y, "------------------------------------------------------");
                    y -= 15;

                    for (ReshelveLine line : report.getLines()) {
                        String row = pad(line.getItemCode(), 8) + " "
                                + pad(line.getItemName(), 20) + " "
                                + line.getQuantityToReshelve();
                        writeLine(cs, 50, y, row);
                        y -= 15;

                        if (y < 60) break;
                    }
                }

                doc.save(out.toFile());
            }

            return out;

        } catch (Exception e) {
            throw new RuntimeException("Failed to export Reshelve Report PDF", e);
        }
    }

    @Override
    public Path exportStock(StockReport report) {
        try {
            Files.createDirectories(outputDir);

            String fileName = "stock_batch_report_" + LocalDate.now() + ".pdf";
            Path out = outputDir.resolve(fileName);

            try (PDDocument doc = new PDDocument()) {
                PDPage page = new PDPage();
                doc.addPage(page);

                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {

                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                    cs.newLineAtOffset(50, 750);
                    cs.showText("SYOS - Stock Batch Report");
                    cs.endText();

                    int y = 720;

                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 12);
                    cs.newLineAtOffset(50, y);
                    cs.showText("Generated: " + LocalDate.now());
                    cs.endText();

                    y -= 25;

                    writeLine(cs, 50, y, "CODE     PURCHASE     EXPIRY       RECEIVED  REMAINING");
                    y -= 15;
                    writeLine(cs, 50, y, "------------------------------------------------------");
                    y -= 15;

                    for (StockBatchLine b : report.getBatches()) {
                        String row = pad(b.getItemCode(), 8) + " "
                                + pad(String.valueOf(b.getPurchaseDate()), 12) + " "
                                + pad(String.valueOf(b.getExpiryDate()), 12) + " "
                                + pad(String.valueOf(b.getReceivedQuantity()), 8) + " "
                                + b.getRemainingQuantity();
                        writeLine(cs, 50, y, row);
                        y -= 15;

                        if (y < 60) break;
                    }
                }

                doc.save(out.toFile());
            }

            return out;

        } catch (Exception e) {
            throw new RuntimeException("Failed to export Stock Report PDF", e);
        }
    }

    @Override
    public Path exportBillReport(LocalDate date, BillReport report) {
        try {
            Files.createDirectories(outputDir);

            String fileName = "bill_report_" + date + ".pdf";
            Path out = outputDir.resolve(fileName);

            try (PDDocument doc = new PDDocument()) {
                PDPage page = new PDPage();
                doc.addPage(page);

                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {

                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                    cs.newLineAtOffset(50, 750);
                    cs.showText("SYOS - Bill Report");
                    cs.endText();

                    int y = 720;

                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 12);
                    cs.newLineAtOffset(50, y);
                    cs.showText("Date: " + date);
                    cs.endText();

                    y -= 25;

                    writeLine(cs, 50, y, "SERIAL    FINAL_TOTAL   TENDERED    CHANGE");
                    y -= 15;
                    writeLine(cs, 50, y, "------------------------------------------------------");
                    y -= 15;

                    for (var b : report.getBills()) {
                        String row = pad(String.valueOf(b.getSerialNumber()), 8) + "  "
                                + pad(b.getFinalTotal().getAmount().toString(), 12) + "  "
                                + pad(b.getCashTendered().getAmount().toString(), 10) + "  "
                                + b.getChangeAmount().getAmount();
                        writeLine(cs, 50, y, row);
                        y -= 15;

                        if (y < 60) break;
                    }
                }

                doc.save(out.toFile());
            }

            return out;

        } catch (Exception e) {
            throw new RuntimeException("Failed to export Bill Report PDF", e);
        }
    }

    private static void writeLine(PDPageContentStream cs, int x, int y, String text) throws Exception {
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 11);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private static String pad(String s, int n) {
        if (s == null) s = "";
        if (s.length() >= n) return s.substring(0, n);
        return s + " ".repeat(n - s.length());
    }
}
