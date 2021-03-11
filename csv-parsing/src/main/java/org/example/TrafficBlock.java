package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TrafficBlock {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public TrafficBlock(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static TrafficBlock fromCsvLine(String csvLine) {
        DateTimeFormatter trafficBlockDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");

        String[] values = csvLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

        LocalDate trafficBlockStartDate = LocalDate.parse(values[10], trafficBlockDateFormat);
        LocalDate trafficBlockEndDate = LocalDate.parse(values[11], trafficBlockDateFormat);

        return new TrafficBlock(trafficBlockStartDate, trafficBlockEndDate);
    }

    public boolean wasActiveOnDate(LocalDate date) {
        return date.isAfter(this.getStartDate()) && date.isBefore(this.getEndDate());
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
