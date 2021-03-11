package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.Stream;

public class Main {
    private static final String WRONG_NUM_OF_ARGUMENTS_ERR_MSG = "Need to specify path to csv file and date (dd.MM.yyyy)";
    private static final String WRONG_DATE_FORMAT_ERR_MSG = "Date should be in format: dd.MM.yyyy";

    public static void main(String[] args) {
        Input input = parseArguments(args);

        long result = getNumberOfTrafficBlocks(input.getPathToCsvFile(), input.getDate());

        System.out.printf("There were %d traffic blocks in %s", result, input.getDate());
    }

    public static long getNumberOfTrafficBlocks(String pathToCsvFile, LocalDate inputDate) {
        try (Stream<String> csvLines = Files.lines(Paths.get(pathToCsvFile))) {
            return csvLines
                    .skip(1) // skip csv file header
                    .map(TrafficBlock::fromCsvLine)
                    .filter(trafficBlock -> trafficBlock.wasActiveOnDate(inputDate))
                    .count();
        }
        catch (IOException e) {
            throw new RuntimeException("Error reading csv file", e);
        }
    }

    private static Input parseArguments(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException(WRONG_NUM_OF_ARGUMENTS_ERR_MSG);
        }

        String pathToCsvFile = args[0];
        LocalDate inputDate;

        try {
            inputDate = LocalDate.parse(args[1], DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        }
        catch (DateTimeParseException e) {
            throw new IllegalArgumentException(WRONG_DATE_FORMAT_ERR_MSG);
        }

        return new Input(pathToCsvFile, inputDate);
    }

}
