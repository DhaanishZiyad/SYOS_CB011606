package com.sypos.application.dto.reports;

public final class ReshelveLine {
    private final String itemCode;
    private final String itemName;
    private final int quantityToReshelve;

    public ReshelveLine(String itemCode, String itemName, int quantityToReshelve) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.quantityToReshelve = quantityToReshelve;
    }

    public String getItemCode() { return itemCode; }
    public String getItemName() { return itemName; }
    public int getQuantityToReshelve() { return quantityToReshelve; }
}
