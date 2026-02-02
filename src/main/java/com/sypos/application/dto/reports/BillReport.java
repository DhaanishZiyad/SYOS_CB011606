package com.sypos.application.dto.reports;

import com.sypos.domain.entities.Bill;

import java.util.List;

public final class BillReport {
    private final List<Bill> bills;

    public BillReport(List<Bill> bills) {
        this.bills = List.copyOf(bills);
    }

    public List<Bill> getBills() { return bills; }
}
