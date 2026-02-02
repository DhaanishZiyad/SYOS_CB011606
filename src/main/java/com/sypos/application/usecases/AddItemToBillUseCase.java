package com.sypos.application.usecases;

import com.sypos.application.ports.ItemRepository;
import com.sypos.domain.entities.Bill;
import com.sypos.domain.entities.BillLineItem;
import com.sypos.domain.entities.Item;
import com.sypos.domain.valueobjects.ItemCode;
import com.sypos.domain.valueobjects.Quantity;

import java.util.Objects;

public class AddItemToBillUseCase {

    private final ItemRepository itemRepository;

    public AddItemToBillUseCase(ItemRepository itemRepository) {
        this.itemRepository = Objects.requireNonNull(itemRepository);
    }

    public void addItem(Bill bill, ItemCode itemCode, Quantity quantity) {
        Objects.requireNonNull(bill);
        Objects.requireNonNull(itemCode);
        Objects.requireNonNull(quantity);

        Item item = itemRepository.findByCode(itemCode)
                .orElseThrow(() -> new IllegalArgumentException("Item not found for code: " + itemCode));

        BillLineItem lineItem = new BillLineItem(item, quantity);
        bill.addItem(lineItem);
    }
}
