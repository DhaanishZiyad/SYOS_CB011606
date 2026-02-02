package com.sypos.domain.entities;

import com.sypos.domain.valueobjects.Money;
import com.sypos.domain.valueobjects.Quantity;

public class BillLineItem {

    private final Item item;
    private final Quantity quantity;

    public BillLineItem(Item item, Quantity quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Money getLineTotal() {
        return item.getUnitPrice().multiply(quantity.getValue());
    }

    public Item getItem() {
        return item;
    }

    public Quantity getQuantity() {
        return quantity;
    }
}
