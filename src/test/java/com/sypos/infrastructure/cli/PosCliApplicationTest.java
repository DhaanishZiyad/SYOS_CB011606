package com.sypos.infrastructure.cli;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PosCliApplicationTest {

    @Test
    void applicationStartsAndExitsImmediately() {
        // Simulate user entering "0" to exit
        ByteArrayInputStream in =
                new ByteArrayInputStream("0\n".getBytes());

        // Capture output (avoid clutter)
        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        PrintStream originalOut = System.out;
        java.io.InputStream originalIn = System.in;

        try {
            System.setIn(in);
            System.setOut(new PrintStream(out));

            assertDoesNotThrow(() ->
                    PosCliApplication.main(new String[]{})
            );

        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}
