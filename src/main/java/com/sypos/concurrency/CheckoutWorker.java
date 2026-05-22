package com.sypos.concurrency;

import com.sypos.adapters.controllers.PosController;
import com.sypos.concurrency.SystemMetrics;
import com.sypos.websocket.MetricsWebSocket;
import com.sypos.websocket.InventoryWebSocket;

public class CheckoutWorker implements Runnable {

    private final PosController controller;

    public CheckoutWorker(PosController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {

        while (true) {
            try {

                CheckoutTask task =
                        CheckoutQueueManager.getQueue().take();

                MetricsWebSocket.broadcast();

                System.out.println(
                        "[" + Thread.currentThread().getName() + "] "
                                + "Processing bill "
                                + task.bill().getSerialNumber()
                                + " | Queue size: "
                                + CheckoutQueueManager.getQueue().size()
                );

                Thread.sleep(3000);

                System.out.println(
                        "Attempting checkout for bill "
                                + task.bill().getSerialNumber()
                );

                controller.checkout(
                        task.bill(),
                        task.tendered()
                );

                InventoryWebSocket.broadcast();

                System.out.println(
                        "Checkout completed for bill "
                                + task.bill().getSerialNumber()
                );

                SystemMetrics.totalProcessedBills.incrementAndGet();

                MetricsWebSocket.broadcast();

                System.out.println(
                        "[" + Thread.currentThread().getName() + "] "
                                + "Finished bill "
                                + task.bill().getSerialNumber()
                );

            } catch (Exception e) {

                System.out.println(
                        "WORKER ERROR:"
                );

                e.printStackTrace();
            }
        }
    }
}