package com.sypos.application.dto.reports;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReorderLineTest {

    @Test
    void createsReorderLineCorrectly() {
        ReorderLine line = new ReorderLine(
                "BREAD001",
                "Bread",
                20
        );

        assertEquals("BREAD001", line.getItemCode());
        assertEquals("Bread", line.getItemName());
        assertEquals(20, line.getCurrentStock());
    }
}
