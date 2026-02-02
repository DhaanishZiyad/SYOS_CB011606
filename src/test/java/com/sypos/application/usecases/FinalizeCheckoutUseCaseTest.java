package com.sypos.application.usecases;

import com.sypos.application.dto.CheckoutResult;
import com.sypos.domain.entities.Bill;
import com.sypos.domain.entities.BillLineItem;
import com.sypos.domain.entities.Item;
import com.sypos.domain.entities.ShelfStock;
import com.sypos.domain.entities.StockBatch;
import com.sypos.domain.inventory.BatchConsumer;
import com.sypos.domain.inventory.ExpiryAwareFifoPolicy;
import com.sypos.domain.payment.CashPaymentStrategy;
import com.sypos.domain.valueobjects.ItemCode;
import com.sypos.domain.valueobjects.Money;
import com.sypos.domain.valueobjects.Quantity;
import com.sypos.testdoubles.FakeBillRepository;
import com.sypos.testdoubles.FakeInventoryRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FinalizeCheckoutUseCaseTest {

    private static Item item(String code, String name, String price) {
        return new Item(new ItemCode(code), name, new Money(new BigDecimal(price)));
    }

    private static StockBatch batch(long id, String code, String purchase, String expiry, int remaining) {
        return new StockBatch(
                id,
                new ItemCode(code),
                LocalDate.parse(purchase),
                LocalDate.parse(expiry),
                new Quantity(remaining)
        );
    }

    private FinalizeCheckoutUseCase buildUseCase(FakeInventoryRepository inv, FakeBillRepository bills) {
        var consumer = new BatchConsumer(new ExpiryAwareFifoPolicy());
        var payment = new CashPaymentStrategy();
        return new FinalizeCheckoutUseCase(inv, bills, consumer, payment);
    }

    @Test
    void throwsIfBillIsNull() {
        var inv = new FakeInventoryRepository();
        var bills = new FakeBillRepository();
        var uc = buildUseCase(inv, bills);

        assertThrows(NullPointerException.class,
                () -> uc.finalizeSale(null, new Money(new BigDecimal("10.00"))));
    }

    @Test
    void throwsIfCashTenderedIsNull() {
        var inv = new FakeInventoryRepository();
        var bills = new FakeBillRepository();
        var uc = buildUseCase(inv, bills);

        Bill bill = new Bill(1, LocalDate.now());
        assertThrows(NullPointerException.class, () -> uc.finalizeSale(bill, null));
    }

    @Test
    void throwsWhenShelfStockMissingForItem() {
        var inv = new FakeInventoryRepository();
        var bills = new FakeBillRepository();
        var uc = buildUseCase(inv, bills);

        Bill bill = new Bill(1, LocalDate.now());
        bill.addItem(new BillLineItem(item("MILK001", "Milk", "100.00"), new Quantity(1)));

        assertThrows(IllegalStateException.class,
                () -> uc.finalizeSale(bill, new Money(new BigDecimal("200.00"))));
    }

    @Test
    void throwsWhenInsufficientShelfStock() {
        var inv = new FakeInventoryRepository();
        var bills = new FakeBillRepository();
        var uc = buildUseCase(inv, bills);

        inv.putShelf(new ShelfStock(new ItemCode("MILK001"), new Quantity(2)));

        Bill bill = new Bill(1, LocalDate.now());
        bill.addItem(new BillLineItem(item("MILK001", "Milk", "100.00"), new Quantity(3)));

        assertThrows(IllegalStateException.class,
                () -> uc.finalizeSale(bill, new Money(new BigDecimal("500.00"))));
    }

    @Test
    void reducesShelfStockOnSuccessfulCheckout() {
        var inv = new FakeInventoryRepository();
        var bills = new FakeBillRepository();
        var uc = buildUseCase(inv, bills);

        inv.putShelf(new ShelfStock(new ItemCode("MILK001"), new Quantity(10)));
        inv.putBatches(new ItemCode("MILK001"), List.of(
                batch(1, "MILK001", "2026-01-01", "2026-02-01", 100)
        ));

        Bill bill = new Bill(1, LocalDate.now());
        bill.addItem(new BillLineItem(item("MILK001", "Milk", "100.00"), new Quantity(3)));

        uc.finalizeSale(bill, new Money(new BigDecimal("500.00")));

        assertEquals(7, inv.getShelf(new ItemCode("MILK001")).getQuantity().getValue());
        assertTrue(inv.saveShelfCalls >= 1);
    }

    @Test
    void consumesFromBatchesOnSuccessfulCheckout() {
        var inv = new FakeInventoryRepository();
        var bills = new FakeBillRepository();
        var uc = buildUseCase(inv, bills);

        inv.putShelf(new ShelfStock(new ItemCode("MILK001"), new Quantity(10)));

        var b1 = batch(1, "MILK001", "2026-01-01", "2026-02-01", 10);
        inv.putBatches(new ItemCode("MILK001"), List.of(b1));

        Bill bill = new Bill(1, LocalDate.now());
        bill.addItem(new BillLineItem(item("MILK001", "Milk", "100.00"), new Quantity(4)));

        uc.finalizeSale(bill, new Money(new BigDecimal("500.00")));

        assertEquals(6, b1.getQuantity().getValue());
        assertTrue(inv.saveBatchesCalls >= 1);
    }

    @Test
    void expiryRuleIsUsed_newerBatchWithEarlierExpiryIsConsumed() {
        var inv = new FakeInventoryRepository();
        var bills = new FakeBillRepository();
        var uc = buildUseCase(inv, bills);

        inv.putShelf(new ShelfStock(new ItemCode("MILK001"), new Quantity(10)));

        var fifo = batch(1, "MILK001", "2026-01-01", "2026-02-01", 100);
        var earlierExpiry = batch(2, "MILK001", "2026-01-10", "2026-01-25", 50);

        inv.putBatches(new ItemCode("MILK001"), List.of(fifo, earlierExpiry));

        Bill bill = new Bill(1, LocalDate.now());
        bill.addItem(new BillLineItem(item("MILK001", "Milk", "100.00"), new Quantity(3)));

        uc.finalizeSale(bill, new Money(new BigDecimal("500.00")));

        assertEquals(100, fifo.getQuantity().getValue());
        assertEquals(47, earlierExpiry.getQuantity().getValue());
    }

    @Test
    void throwsWhenInsufficientBatchStockEvenIfShelfIsEnough() {
        var inv = new FakeInventoryRepository();
        var bills = new FakeBillRepository();
        var uc = buildUseCase(inv, bills);

        inv.putShelf(new ShelfStock(new ItemCode("MILK001"), new Quantity(10)));
        inv.putBatches(new ItemCode("MILK001"), List.of(
                batch(1, "MILK001", "2026-01-01", "2026-02-01", 2)
        ));

        Bill bill = new Bill(1, LocalDate.now());
        bill.addItem(new BillLineItem(item("MILK001", "Milk", "100.00"), new Quantity(5)));

        assertThrows(IllegalStateException.class,
                () -> uc.finalizeSale(bill, new Money(new BigDecimal("1000.00"))));
    }

    @Test
    void processesPaymentAndReturnsCheckoutResult() {
        var inv = new FakeInventoryRepository();
        var bills = new FakeBillRepository();
        var uc = buildUseCase(inv, bills);

        inv.putShelf(new ShelfStock(new ItemCode("MILK001"), new Quantity(10)));
        inv.putBatches(new ItemCode("MILK001"), List.of(
                batch(1, "MILK001", "2026-01-01", "2026-02-01", 100)
        ));

        Bill bill = new Bill(1, LocalDate.now());
        bill.addItem(new BillLineItem(item("MILK001", "Milk", "100.00"), new Quantity(2))); // total 200

        CheckoutResult result = uc.finalizeSale(bill, new Money(new BigDecimal("500.00")));

        assertNotNull(result);
        assertEquals(new BigDecimal("300.00"), result.getPaymentResult().getChange().getAmount());
    }

    @Test
    void throwsWhenTenderedLessThanAmountDue() {
        var inv = new FakeInventoryRepository();
        var bills = new FakeBillRepository();
        var uc = buildUseCase(inv, bills);

        inv.putShelf(new ShelfStock(new ItemCode("MILK001"), new Quantity(10)));
        inv.putBatches(new ItemCode("MILK001"), List.of(
                batch(1, "MILK001", "2026-01-01", "2026-02-01", 100)
        ));

        Bill bill = new Bill(1, LocalDate.now());
        bill.addItem(new BillLineItem(item("MILK001", "Milk", "100.00"), new Quantity(2))); // total 200

        assertThrows(IllegalArgumentException.class,
                () -> uc.finalizeSale(bill, new Money(new BigDecimal("199.99"))));
    }

    @Test
    void savesBillExactlyOnceOnSuccessfulCheckout() {
        var inv = new FakeInventoryRepository();
        var bills = new FakeBillRepository();
        var uc = buildUseCase(inv, bills);

        inv.putShelf(new ShelfStock(new ItemCode("MILK001"), new Quantity(10)));
        inv.putBatches(new ItemCode("MILK001"), List.of(
                batch(1, "MILK001", "2026-01-01", "2026-02-01", 100)
        ));

        Bill bill = new Bill(1, LocalDate.now());
        bill.addItem(new BillLineItem(item("MILK001", "Milk", "100.00"), new Quantity(1)));

        uc.finalizeSale(bill, new Money(new BigDecimal("200.00")));

        assertEquals(1, bills.saveCalls);
        assertSame(bill, bills.lastSaved);
    }

    @Test
    void doesNotSaveBillIfShelfValidationFails() {
        var inv = new FakeInventoryRepository();
        var bills = new FakeBillRepository();
        var uc = buildUseCase(inv, bills);

        inv.putShelf(new ShelfStock(new ItemCode("MILK001"), new Quantity(1)));

        Bill bill = new Bill(1, LocalDate.now());
        bill.addItem(new BillLineItem(item("MILK001", "Milk", "100.00"), new Quantity(2)));

        assertThrows(IllegalStateException.class,
                () -> uc.finalizeSale(bill, new Money(new BigDecimal("500.00"))));

        assertEquals(0, bills.saveCalls);
    }
}
