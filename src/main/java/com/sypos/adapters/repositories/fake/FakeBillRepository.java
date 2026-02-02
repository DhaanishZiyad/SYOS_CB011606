package com.sypos.adapters.repositories.fake;

import com.sypos.application.ports.BillRepository;
import com.sypos.domain.entities.Bill;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakeBillRepository implements BillRepository {

    private int lastSerial = 0;
    private final Map<Integer, Bill> bills = new HashMap<>();

    @Override
    public int nextSerialNumber() {
        lastSerial++;
        return lastSerial;
    }

    @Override
    public void save(Bill bill) {
        bills.put(bill.getSerialNumber(), bill);
    }

    @Override
    public Optional<Bill> findBySerial(int serialNumber) {
        return Optional.ofNullable(bills.get(serialNumber));
    }

    @Override
    public List<Bill> findByDate(LocalDate date) {
        return bills.values().stream()
                .filter(b -> b.getDate().equals(date))
                .toList();
    }
}