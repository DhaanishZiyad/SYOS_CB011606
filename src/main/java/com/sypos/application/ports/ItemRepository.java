package com.sypos.application.ports;

import com.sypos.domain.entities.Item;
import com.sypos.domain.valueobjects.ItemCode;

import java.util.Optional;
import java.util.List;

public interface ItemRepository {
    Optional<Item> findByCode(ItemCode code);
    List<Item> findAll();
}
