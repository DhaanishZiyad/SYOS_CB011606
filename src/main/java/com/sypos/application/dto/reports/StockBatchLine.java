package com.sypos.application.dto.reports;

import java.time.LocalDate;

public final class StockBatchLine {
    private final String itemCode;
    private final LocalDate purchaseDate;
    private final int receivedQuantity;
    private final int remainingQuantity;
    private final LocalDate expiryDate;

    public StockBatchLine(String itemCode, LocalDate purchaseDate, int receivedQuantity, int remainingQuantity, LocalDate expiryDate) {
        this.itemCode = itemCode;
        this.purchaseDate = purchaseDate;
        this.receivedQuantity = receivedQuantity;
        this.remainingQuantity = remainingQuantity;
        this.expiryDate = expiryDate;
    }

    public String getItemCode() { return itemCode; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public int getReceivedQuantity() { return receivedQuantity; }
    public int getRemainingQuantity() { return remainingQuantity; }
    public LocalDate getExpiryDate() { return expiryDate; }
}
