package com.sypos.application.usecases;

import com.sypos.domain.entities.Bill;
import com.sypos.domain.entities.BillLineItem;
import com.sypos.domain.entities.Item;
import com.sypos.domain.valueobjects.ItemCode;
import com.sypos.domain.valueobjects.Money;
import com.sypos.domain.valueobjects.Quantity;
import com.sypos.testdoubles.FakeItemRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AddItemToBillUseCaseTest {

    private static Item item(String code, String name, String price) {
        return new Item(new ItemCode(code), name, new Money(new BigDecimal(price)));
    }

    private AddItemToBillUseCase build(FakeItemRepository repo) {
        return new AddItemToBillUseCase(repo);
    }

    @Test
    void throwsIfBillIsNull() {
        var uc = build(new FakeItemRepository());

        assertThrows(NullPointerException.class,
                () -> uc.addItem(null, new ItemCode("MILK001"), new Quantity(1)));
    }

    @Test
    void throwsIfItemCodeIsNull() {
        var uc = build(new FakeItemRepository());
        Bill bill = new Bill(1, LocalDate.now());

        assertThrows(NullPointerException.class,
                () -> uc.addItem(bill, null, new Quantity(1)));
    }

    @Test
    void throwsIfQuantityIsNull() {
        var uc = build(new FakeItemRepository());
        Bill bill = new Bill(1, LocalDate.now());

        assertThrows(NullPointerException.class,
                () -> uc.addItem(bill, new ItemCode("MILK001"), null));
    }

    @Test
    void addsItemToBillWhenValid() {
        FakeItemRepository repo = new FakeItemRepository();
        repo.put(item("MILK001", "Milk", "100.00"));

        var uc = build(repo);
        Bill bill = new Bill(1, LocalDate.now());

        uc.addItem(bill, new ItemCode("MILK001"), new Quantity(2));

        assertEquals(1, bill.getItems().size());
        assertEquals(new BigDecimal("200.00"), bill.getTotal().getAmount());
    }

    @Test
    void addsCorrectQuantityToBillLineItem() {
        FakeItemRepository repo = new FakeItemRepository();
        repo.put(item("MILK001", "Milk", "100.00"));

        var uc = build(repo);
        Bill bill = new Bill(1, LocalDate.now());

        uc.addItem(bill, new ItemCode("MILK001"), new Quantity(3));

        BillLineItem line = bill.getItems().get(0);
        assertEquals(3, line.getQuantity().getValue());
    }

    @Test
    void addsMultipleDifferentItems() {
        FakeItemRepository repo = new FakeItemRepository();
        repo.put(item("MILK001", "Milk", "100.00"));
        repo.put(item("BREAD001", "Bread", "50.00"));

        var uc = build(repo);
        Bill bill = new Bill(1, LocalDate.now());

        uc.addItem(bill, new ItemCode("MILK001"), new Quantity(1));
        uc.addItem(bill, new ItemCode("BREAD001"), new Quantity(2));

        assertEquals(2, bill.getItems().size());
        assertEquals(new BigDecimal("200.00"), bill.getTotal().getAmount());
    }

    @Test
    void sameItemAddedTwiceCreatesTwoLineItems() {
        FakeItemRepository repo = new FakeItemRepository();
        repo.put(item("MILK001", "Milk", "100.00"));

        var uc = build(repo);
        Bill bill = new Bill(1, LocalDate.now());

        uc.addItem(bill, new ItemCode("MILK001"), new Quantity(1));
        uc.addItem(bill, new ItemCode("MILK001"), new Quantity(2));

        assertEquals(2, bill.getItems().size());
        assertEquals(new BigDecimal("300.00"), bill.getTotal().getAmount());
    }

    @Test
    void quantityValueObjectRejectsNegativeBeforeUseCase() {
        assertThrows(IllegalArgumentException.class,
                () -> new Quantity(-5));
    }
}
