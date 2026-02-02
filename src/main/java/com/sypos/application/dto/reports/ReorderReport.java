package com.sypos.application.dto.reports;

import java.util.List;

public final class ReorderReport {
    private final List<ReorderLine> lines;

    public ReorderReport(List<ReorderLine> lines) {
        this.lines = List.copyOf(lines);
    }

    public List<ReorderLine> getLines() { return lines; }
}
