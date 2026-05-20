package com.sypos.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

public class SystemMetrics {

    public static final AtomicInteger totalQueuedBills =
            new AtomicInteger(0);

    public static final AtomicInteger totalProcessedBills =
            new AtomicInteger(0);

    public static final AtomicInteger totalHttpRequests =
            new AtomicInteger(0);

    private SystemMetrics() {
    }
}