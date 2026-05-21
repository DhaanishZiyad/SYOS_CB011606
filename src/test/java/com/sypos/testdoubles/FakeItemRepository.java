package com.sypos.testdoubles;

import com.sypos.application.ports.ItemRepository;
import com.sypos.domain.entities.Item;
import com.sypos.domain.valueobjects.ItemCode;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

public class FakeItemRepository implements ItemRepository {

    private final Map<String, Item> items = new HashMap<>();

    public void put(Item item) {
        items.put(item.getCode().getValue(), item);
    }

    @Override
    public Optional<Item> findByCode(ItemCode code) {
        return Optional.ofNullable(items.get(code.getValue()));
    }

    @Override
    public List<Item> findAll() {
        return new java.util.ArrayList<>(items.values());
    }

    @Override
    public void save(Item item) {

    }
}
