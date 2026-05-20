package com.sypos.domain.entities;

import com.sypos.domain.valueobjects.Money;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Bill {

    private final int serialNumber;
    private final LocalDate date;
    private final List<BillLineItem> items = new ArrayList<>();
    private Money total = new Money(java.math.BigDecimal.ZERO);
    private Money discount = Money.zero();
    private Money cashTendered = Money.zero();
    private Money changeAmount = Money.zero();



    public Bill(int serialNumber, LocalDate date) {
        this.serialNumber = serialNumber;
        this.date = date;
    }

    public void addItem(BillLineItem newItem) {

        for (int i = 0; i < items.size(); i++) {
            BillLineItem existing = items.get(i);

            if (existing.getItem().getCode().getValue()
                    .equals(newItem.getItem().getCode().getValue())) {

                int updatedQty = existing.getQuantity().getValue()
                        + newItem.getQuantity().getValue();

                BillLineItem updatedItem = new BillLineItem(
                        existing.getItem(),
                        new com.sypos.domain.valueobjects.Quantity(updatedQty)
                );

                items.set(i, updatedItem);
                recalculateTotal();
                return;
            }
        }

        items.add(newItem);
        recalculateTotal();
    }

    public Money getTotal() {
        return total;
    }

    public void setTotal(Money total) {
        this.total = total;
    }

    public void setDiscount(Money discount) {
        this.discount = discount;
    }

    public List<BillLineItem> getItems() {
        return List.copyOf(items);
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public void applyDiscount(Money discount) {
        this.discount = java.util.Objects.requireNonNull(discount);
    }

    public Money getDiscount() {
        return discount;
    }

    public Money getFinalTotal() {
        return total.subtract(discount);
    }

    public void recordPayment(Money tendered, Money change) {
        this.cashTendered = Objects.requireNonNull(tendered);
        this.changeAmount = Objects.requireNonNull(change);
    }

    public Money getCashTendered() {
        return cashTendered;
    }

    public Money getChangeAmount() {
        return changeAmount;
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            recalculateTotal();
        }
    }

    private void recalculateTotal() {
        total = new Money(java.math.BigDecimal.ZERO);
        for (BillLineItem item : items) {
            total = total.add(item.getLineTotal());
        }
    }
}