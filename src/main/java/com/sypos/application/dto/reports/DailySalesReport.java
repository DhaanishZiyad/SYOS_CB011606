package com.sypos.application.dto.reports;

import java.time.LocalDate;
import java.util.List;

public final class DailySalesReport {
    private final LocalDate date;
    private final List<SalesLine> lines;

    public DailySalesReport(LocalDate date, List<SalesLine> lines) {
        this.date = date;
        this.lines = List.copyOf(lines);
    }

    public LocalDate getDate() { return date; }
    public List<SalesLine> getLines() { return lines; }
}
