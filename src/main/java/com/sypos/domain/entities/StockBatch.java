package com.sypos.domain.entities;

import com.sypos.domain.valueobjects.ItemCode;
import com.sypos.domain.valueobjects.Quantity;

import java.time.LocalDate;

public class StockBatch {

    private final long id;
    private final ItemCode itemCode;
    private final LocalDate purchaseDate;
    private final LocalDate expiryDate;
    private Quantity quantity;

    public StockBatch(long id, ItemCode itemCode, LocalDate purchaseDate,
                      LocalDate expiryDate, Quantity quantity) {
        this.id = id;
        this.itemCode = itemCode;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public ItemCode getItemCode() {
        return itemCode;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public void reduceQuantity(Quantity amount) {
        java.util.Objects.requireNonNull(amount);
        this.quantity = this.quantity.subtract(amount);
    }
}
