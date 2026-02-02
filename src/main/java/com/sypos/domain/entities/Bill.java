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

    public void addItem(BillLineItem item) {
        items.add(item);
        total = total.add(item.getLineTotal());
    }

    public Money getTotal() {
        return total;
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
}