package com.sypos.application.dto.reports;

import com.sypos.domain.valueobjects.Money;

public final class SalesLine {
    private final String itemCode;
    private final String itemName;
    private final int totalQuantity;
    private final Money totalRevenue;

    public SalesLine(String itemCode, String itemName, int totalQuantity, Money totalRevenue) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    public String getItemCode() { return itemCode; }
    public String getItemName() { return itemName; }
    public int getTotalQuantity() { return totalQuantity; }
    public Money getTotalRevenue() { return totalRevenue; }
}
