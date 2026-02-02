package com.sypos.domain.entities;

import com.sypos.domain.valueobjects.ItemCode;
import com.sypos.domain.valueobjects.Money;
import com.sypos.domain.valueobjects.Quantity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BillTest {

    private static Item item(String code, String name, String price) {
        return new Item(new ItemCode(code), name, new Money(new BigDecimal(price)));
    }

    @Test
    void newBillHasZeroTotalsAndNoItems() {
        Bill bill = new Bill(1, LocalDate.now());
        assertEquals(new BigDecimal("0.00"), bill.getTotal().getAmount());
        assertTrue(bill.getItems().isEmpty());
        assertEquals(new BigDecimal("0.00"), bill.getDiscount().getAmount());
        assertEquals(new BigDecimal("0.00"), bill.getCashTendered().getAmount());
        assertEquals(new BigDecimal("0.00"), bill.getChangeAmount().getAmount());
    }

    @Test
    void addItemIncreasesTotal() {
        Bill bill = new Bill(1, LocalDate.now());

        bill.addItem(new BillLineItem(item("MILK001", "Milk", "100.00"), new Quantity(2)));

        assertEquals(new BigDecimal("200.00"), bill.getTotal().getAmount());
        assertEquals(1, bill.getItems().size());
    }

    @Test
    void addMultipleItemsAccumulatesTotal() {
        Bill bill = new Bill(1, LocalDate.now());

        bill.addItem(new BillLineItem(item("MILK001", "Milk", "100.00"), new Quantity(2)));  // 200
        bill.addItem(new BillLineItem(item("BREAD001", "Bread", "50.00"), new Quantity(3))); // 150

        assertEquals(new BigDecimal("350.00"), bill.getTotal().getAmount());
        assertEquals(2, bill.getItems().size());
    }

    @Test
    void getFinalTotalEqualsTotalWhenNoDiscount() {
        Bill bill = new Bill(1, LocalDate.now());
        bill.addItem(new BillLineItem(item("MILK001", "Milk", "100.00"), new Quantity(2))); // 200

        assertEquals(new BigDecimal("200.00"), bill.getFinalTotal().getAmount());
    }

    @Test
    void applyDiscountReducesFinalTotal() {
        Bill bill = new Bill(1, LocalDate.now());
        bill.addItem(new BillLineItem(item("MILK001", "Milk", "100.00"), new Quantity(2))); // 200

        bill.applyDiscount(new Money(new BigDecimal("20.00")));

        assertEquals(new BigDecimal("20.00"), bill.getDiscount().getAmount());
        assertEquals(new BigDecimal("180.00"), bill.getFinalTotal().getAmount());
    }

    @Test
    void applyZeroDiscountKeepsFinalTotalSame() {
        Bill bill = new Bill(1, LocalDate.now());
        bill.addItem(new BillLineItem(item("MILK001", "Milk", "100.00"), new Quantity(1))); // 100

        bill.applyDiscount(Money.zero());

        assertEquals(new BigDecimal("100.00"), bill.getFinalTotal().getAmount());
    }

    @Test
    void discountGreaterThanTotalCausesFinalTotalToThrow() {
        Bill bill = new Bill(1, LocalDate.now());
        bill.addItem(new BillLineItem(item("MILK001", "Milk", "50.00"), new Quantity(1))); // 50

        bill.applyDiscount(new Money(new BigDecimal("60.00")));

        assertThrows(IllegalArgumentException.class, bill::getFinalTotal);
    }

    @Test
    void recordPaymentStoresTenderedAndChange() {
        Bill bill = new Bill(1, LocalDate.now());

        bill.recordPayment(new Money(new BigDecimal("500.00")), new Money(new BigDecimal("25.00")));

        assertEquals(new BigDecimal("500.00"), bill.getCashTendered().getAmount());
        assertEquals(new BigDecimal("25.00"), bill.getChangeAmount().getAmount());
    }

    @Test
    void recordPaymentRejectsNullTendered() {
        Bill bill = new Bill(1, LocalDate.now());
        assertThrows(NullPointerException.class,
                () -> bill.recordPayment(null, Money.zero()));
    }

    @Test
    void recordPaymentRejectsNullChange() {
        Bill bill = new Bill(1, LocalDate.now());
        assertThrows(NullPointerException.class,
                () -> bill.recordPayment(Money.zero(), null));
    }

    @Test
    void getItemsReturnsUnmodifiableCopy() {
        Bill bill = new Bill(1, LocalDate.now());
        bill.addItem(new BillLineItem(item("MILK001", "Milk", "100.00"), new Quantity(1)));

        var items = bill.getItems();
        assertThrows(UnsupportedOperationException.class, () -> items.add(
                new BillLineItem(item("X", "Y", "1.00"), new Quantity(1))
        ));
    }

    @Test
    void serialAndDateAreStored() {
        LocalDate d = LocalDate.of(2026, 1, 19);
        Bill bill = new Bill(99, d);

        assertEquals(99, bill.getSerialNumber());
        assertEquals(d, bill.getDate());
    }
}
