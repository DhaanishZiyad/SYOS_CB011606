package com.sypos.application.usecases;

import com.sypos.application.dto.CheckoutResult;
import com.sypos.application.ports.BillRepository;
import com.sypos.application.ports.InventoryRepository;
import com.sypos.domain.entities.Bill;
import com.sypos.domain.entities.ShelfStock;
import com.sypos.domain.entities.StockBatch;
import com.sypos.domain.inventory.BatchConsumer;
import com.sypos.domain.payment.PaymentResult;
import com.sypos.domain.payment.PaymentStrategy;
import com.sypos.domain.valueobjects.ItemCode;
import com.sypos.domain.valueobjects.Money;
import com.sypos.domain.valueobjects.Quantity;

import java.util.List;
import java.util.Objects;

public class FinalizeCheckoutUseCase {

    private final InventoryRepository inventoryRepository;
    private final BillRepository billRepository;
    private final BatchConsumer batchConsumer;
    private final PaymentStrategy paymentStrategy;

    public FinalizeCheckoutUseCase(
            InventoryRepository inventoryRepository,
            BillRepository billRepository,
            BatchConsumer batchConsumer,
            PaymentStrategy paymentStrategy
    ) {
        this.inventoryRepository = Objects.requireNonNull(inventoryRepository);
        this.billRepository = Objects.requireNonNull(billRepository);
        this.batchConsumer = Objects.requireNonNull(batchConsumer);
        this.paymentStrategy = Objects.requireNonNull(paymentStrategy);
    }

    /**
     * Finalizes the sale:
     * 1) checks shelf stock for each item
     * 2) reduces shelf stock
     * 3) consumes store batches using expiry-aware FIFO
     * 4) processes payment
     * 5) saves bill + inventory updates
     */
    public CheckoutResult finalizeSale(Bill bill, Money cashTendered) {
        Objects.requireNonNull(bill);
        Objects.requireNonNull(cashTendered);

        // 1) Validate and reduce shelf stock + consume batches for each bill line
        for (var line : bill.getItems()) {
            ItemCode code = line.getItem().getCode();
            Quantity qty = line.getQuantity();

            ShelfStock shelfStock = inventoryRepository.findShelfStock(code)
                    .orElseThrow(() -> new IllegalStateException("Shelf stock not found for item: " + code));

            // Validate shelf has enough
            if (shelfStock.getQuantity().getValue() < qty.getValue()) {
                throw new IllegalStateException("Insufficient shelf stock for item: " + code);
            }

            // Reduce shelf
            shelfStock.reduce(qty);
            inventoryRepository.saveShelfStock(shelfStock);

            // Consume from store batches (expiry-aware FIFO)
            List<StockBatch> batches = inventoryRepository.findAvailableBatches(code);
            List<StockBatch> modified = batchConsumer.consume(code, qty, batches);
            inventoryRepository.saveBatches(modified);
        }

        // 2) Payment
        Money amountDue = bill.getFinalTotal();
        PaymentResult paymentResult = paymentStrategy.pay(amountDue, cashTendered);

        // 3) Save bill (and later bill line items inside JdbcBillRepository)
        billRepository.save(bill);

        return new CheckoutResult(bill, paymentResult);
    }
}
