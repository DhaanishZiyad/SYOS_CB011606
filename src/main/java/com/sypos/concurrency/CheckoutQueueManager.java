package com.sypos.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CheckoutQueueManager {

    private static final BlockingQueue<CheckoutTask> queue =
            new LinkedBlockingQueue<>();

    public static BlockingQueue<CheckoutTask> getQueue() {
        return queue;
    }
}