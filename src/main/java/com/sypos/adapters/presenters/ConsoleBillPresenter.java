package com.sypos.adapters.presenters;

import com.sypos.application.dto.CheckoutResult;
import com.sypos.domain.entities.Bill;
import com.sypos.domain.entities.BillLineItem;

public class ConsoleBillPresenter {

    public void printBill(Bill bill) {
        System.out.println("\n==============================");
        System.out.println("SYOS BILL");
        System.out.println("Serial: " + bill.getSerialNumber());
        System.out.println("Date  : " + bill.getDate());
        System.out.println("------------------------------");
        System.out.printf("%-10s %-15s %5s %10s%n", "CODE", "ITEM", "QTY", "TOTAL");

        for (BillLineItem li : bill.getItems()) {
            System.out.printf("%-10s %-15s %5d %10.2f%n",
                    li.getItem().getCode().getValue(),
                    li.getItem().getName(),
                    li.getQuantity().getValue(),
                    li.getLineTotal().getAmount()
            );
        }

        System.out.println("------------------------------");
        System.out.printf("%-20s %10.2f%n", "Full Total:", bill.getTotal().getAmount());
        System.out.printf("%-20s %10.2f%n", "Discount:", bill.getDiscount().getAmount());
        System.out.printf("%-20s %10.2f%n", "Final Total:", bill.getFinalTotal().getAmount());
        System.out.println("==============================");
    }

    public void printPayment(CheckoutResult result) {
        System.out.printf("%-20s %10.2f%n", "Cash Tendered:", result.getPaymentResult().getTendered().getAmount());
        System.out.printf("%-20s %10.2f%n", "Change:", result.getPaymentResult().getChange().getAmount());
        System.out.println("==============================\n");
    }
}
