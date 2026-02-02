package com.sypos.application.ports;

import com.sypos.domain.entities.Bill;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BillRepository {

    int nextSerialNumber();

    void save(Bill bill);

    Optional<Bill> findBySerial(int serialNumber);

    List<Bill> findByDate(LocalDate date);
}