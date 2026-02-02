package com.sypos.domain.entities;

import com.sypos.domain.valueobjects.ItemCode;
import com.sypos.domain.valueobjects.Quantity;

public class ShelfStock {

    private final ItemCode itemCode;
    private Quantity quantity;

    public ShelfStock(ItemCode itemCode, Quantity quantity) {
        this.itemCode = itemCode;
        this.quantity = quantity;
    }

    public ItemCode getItemCode() {
        return itemCode;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public void reduce(Quantity amount) {
        this.quantity = this.quantity.subtract(amount);
    }

    public boolean needsReorder() {
        return quantity.getValue() < 50;
    }
}
