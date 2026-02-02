package com.sypos.testdoubles;

import com.sypos.application.ports.BillRepository;
import com.sypos.domain.entities.Bill;

import java.time.LocalDate;
import java.util.*;

public class FakeBillRepository implements BillRepository {

    public int saveCalls = 0;
    public Bill lastSaved = null;

    private final Map<Integer, Bill> bySerial = new HashMap<>();

    @Override
    public int nextSerialNumber() {
        return bySerial.size() + 1; // simple in-memory sequence
    }

    @Override
    public void save(Bill bill) {
        saveCalls++;
        lastSaved = bill;
        bySerial.put(bill.getSerialNumber(), bill);
    }

    @Override
    public Optional<Bill> findBySerial(int serialNumber) {
        return Optional.ofNullable(bySerial.get(serialNumber));
    }

    @Override
    public List<Bill> findByDate(LocalDate date) {
        List<Bill> out = new ArrayList<>();
        for (Bill b : bySerial.values()) {
            if (b.getDate().equals(date)) out.add(b);
        }
        out.sort(Comparator.comparingInt(Bill::getSerialNumber));
        return out;
    }
}
