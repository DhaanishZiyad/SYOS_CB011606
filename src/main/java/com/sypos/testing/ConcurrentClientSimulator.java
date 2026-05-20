package com.sypos.testing;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ConcurrentClientSimulator {

    private static final String SERVER_URL =
            "http://localhost:8080/syos/pos?action=simulateCheckout";

    public static void main(String[] args) {

        int clientCount = 10;

        for (int i = 1; i <= clientCount; i++) {

            int clientId = i;

            Thread clientThread = new Thread(() -> {
                simulateClient(clientId);
            });

            clientThread.setName("Client-" + clientId);

            clientThread.start();
        }
    }

    private static void simulateClient(int clientId) {

        try {

            URL url = new URL(SERVER_URL);

            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String params = "tendered=5000";

            try (OutputStream os = connection.getOutputStream()) {

                byte[] input =
                        params.getBytes(StandardCharsets.UTF_8);

                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            System.out.println(
                    Thread.currentThread().getName()
                            + " received response: "
                            + responseCode
            );

        } catch (Exception e) {

            System.out.println(
                    Thread.currentThread().getName()
                            + " failed: "
                            + e.getMessage()
            );
        }
    }
}