package com.sypos.domain.entities;

import com.sypos.domain.valueobjects.ItemCode;
import com.sypos.domain.valueobjects.Quantity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShelfStockTest {

    @Test
    void createsShelfStockWithInitialQuantity() {
        ShelfStock stock = new ShelfStock(
                new ItemCode("MILK001"),
                new Quantity(10)
        );

        assertEquals(10, stock.getQuantity().getValue());
    }

    @Test
    void reduceDecreasesQuantity() {
        ShelfStock stock = new ShelfStock(
                new ItemCode("MILK001"),
                new Quantity(10)
        );

        stock.reduce(new Quantity(3));

        assertEquals(7, stock.getQuantity().getValue());
    }

    @Test
    void reduceToZeroIsAllowed() {
        ShelfStock stock = new ShelfStock(
                new ItemCode("MILK001"),
                new Quantity(5)
        );

        stock.reduce(new Quantity(5));

        assertEquals(0, stock.getQuantity().getValue());
    }

    @Test
    void reduceMoreThanAvailableThrows() {
        ShelfStock stock = new ShelfStock(
                new ItemCode("MILK001"),
                new Quantity(4)
        );

        assertThrows(IllegalArgumentException.class,
                () -> stock.reduce(new Quantity(10)));
    }

    @Test
    void reduceWithZeroDoesNothing() {
        ShelfStock stock = new ShelfStock(
                new ItemCode("MILK001"),
                new Quantity(8)
        );

        stock.reduce(new Quantity(0));

        assertEquals(8, stock.getQuantity().getValue());
    }

    @Test
    void itemCodeDoesNotChangeWhenReducing() {
        ItemCode code = new ItemCode("MILK001");
        ShelfStock stock = new ShelfStock(code, new Quantity(10));

        stock.reduce(new Quantity(2));

        assertEquals(code, stock.getItemCode());
    }

    @Test
    void needsReorderReturnsTrueWhenBelowThreshold() {
        ShelfStock stock = new ShelfStock(
                new ItemCode("MILK001"),
                new Quantity(49)
        );

        assertTrue(stock.needsReorder());
    }

    @Test
    void needsReorderReturnsFalseWhenAtThreshold() {
        ShelfStock stock = new ShelfStock(
                new ItemCode("MILK001"),
                new Quantity(50)
        );

        assertFalse(stock.needsReorder());
    }

    @Test
    void needsReorderReturnsFalseWhenAboveThreshold() {
        ShelfStock stock = new ShelfStock(
                new ItemCode("MILK001"),
                new Quantity(100)
        );

        assertFalse(stock.needsReorder());
    }
}
