package com.sypos.application.dto.reports;

import java.util.List;

public final class ReshelveReport {
    private final List<ReshelveLine> lines;

    public ReshelveReport(List<ReshelveLine> lines) {
        this.lines = List.copyOf(lines);
    }

    public List<ReshelveLine> getLines() { return lines; }
}
