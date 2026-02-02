package com.sypos.domain.entities;

import com.sypos.domain.valueobjects.ItemCode;
import com.sypos.domain.valueobjects.Money;

public class Item {

    private final ItemCode code;
    private final String name;
    private final Money unitPrice;

    public Item(ItemCode code, String name, Money unitPrice) {
        this.code = code;
        this.name = name;
        this.unitPrice = unitPrice;
    }

    public ItemCode getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }
}
