package com.sypos.concurrency;

import java.util.concurrent.atomic.AtomicInteger;
import com.sypos.concurrency.CheckoutQueueManager;

public class SystemMetrics {

    public static final AtomicInteger totalQueuedBills =
            new AtomicInteger(0);

    public static final AtomicInteger totalProcessedBills =
            new AtomicInteger(0);

    public static final AtomicInteger totalHttpRequests =
            new AtomicInteger(0);

    private SystemMetrics() {
    }

    public static String toJson() {

        return "{"
                + "\"queueSize\":"
                + CheckoutQueueManager
                .getQueue()
                .size()
                + ","

                + "\"processedBills\":"
                + totalProcessedBills.get()
                + ","

                + "\"queuedBills\":"
                + totalQueuedBills.get()

                + "}";
    }
}