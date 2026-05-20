package com.sypos.concurrency;

import com.sypos.adapters.controllers.PosController;
import com.sypos.concurrency.SystemMetrics;

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

                System.out.println(
                        "[" + Thread.currentThread().getName() + "] "
                                + "Processing bill "
                                + task.bill().getSerialNumber()
                                + " | Queue size: "
                                + CheckoutQueueManager.getQueue().size()
                );

                controller.checkout(
                        task.bill(),
                        task.tendered()
                );

                SystemMetrics.totalProcessedBills.incrementAndGet();

                Thread.sleep(3000);

                System.out.println(
                        "[" + Thread.currentThread().getName() + "] "
                                + "Finished bill "
                                + task.bill().getSerialNumber()
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}