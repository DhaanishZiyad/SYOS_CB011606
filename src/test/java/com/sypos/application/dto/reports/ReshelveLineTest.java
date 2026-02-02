package com.sypos.application.dto.reports;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReshelveLineTest {

    @Test
    void createsReshelveLineCorrectly() {
        ReshelveLine line = new ReshelveLine(
                "MILK001",
                "Milk 1L",
                30
        );

        assertEquals("MILK001", line.getItemCode());
        assertEquals("Milk 1L", line.getItemName());
        assertEquals(30, line.getQuantityToReshelve());
    }
}
