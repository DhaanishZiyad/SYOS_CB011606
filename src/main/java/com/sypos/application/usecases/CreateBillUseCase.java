package com.sypos.application.usecases;

import com.sypos.application.ports.BillRepository;
import com.sypos.domain.entities.Bill;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

public class CreateBillUseCase {

    private final BillRepository billRepository;
    private final Clock clock;

    public CreateBillUseCase(BillRepository billRepository, Clock clock) {
        this.billRepository = Objects.requireNonNull(billRepository);
        this.clock = Objects.requireNonNull(clock);
    }

    public Bill createNewBill() {
        int serial = billRepository.nextSerialNumber();
        LocalDate today = LocalDate.now(clock);
        return new Bill(serial, today);
    }
}
