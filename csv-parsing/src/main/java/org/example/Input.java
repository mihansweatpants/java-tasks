package org.example;

import java.time.LocalDate;

public class Input {
    private final String pathToCsvFile;
    private final LocalDate date;

    public Input(String pathToCsvFile, LocalDate date) {
        this.pathToCsvFile = pathToCsvFile;
        this.date = date;
    }

    public String getPathToCsvFile() {
        return pathToCsvFile;
    }

    public LocalDate getDate() {
        return date;
    }
}
