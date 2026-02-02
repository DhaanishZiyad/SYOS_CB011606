package com.sypos.application.dto.reports;

public final class ReorderLine {
    private final String itemCode;
    private final String itemName;
    private final int currentStock;

    public ReorderLine(String itemCode, String itemName, int currentStock) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.currentStock = currentStock;
    }

    public String getItemCode() { return itemCode; }
    public String getItemName() { return itemName; }
    public int getCurrentStock() { return currentStock; }
}