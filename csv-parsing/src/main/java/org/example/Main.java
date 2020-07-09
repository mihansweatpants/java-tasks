package org.example;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {
    public static String WRONG_NUM_OF_ARGUMENTS_ERR_MSG = "Need to specify path to csv file and date (dd.MM.yyyy)";
    public static String WRONG_DATE_FORMAT_ERR_MSG = "Date should be in format: dd.MM.yyyy";

    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException(WRONG_NUM_OF_ARGUMENTS_ERR_MSG);
        }

        String pathToCsvFile = args[0];
        LocalDate inputDate = null;

        try {
            inputDate = LocalDate.parse(args[1], DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        }
        catch (DateTimeParseException e) {
            throw new IllegalArgumentException(WRONG_DATE_FORMAT_ERR_MSG);
        }

        System.out.printf("There were %d traffic blocks in %s", getNumberOfTrafficBlocks(pathToCsvFile, inputDate), args[1]);
    }

    public static int getNumberOfTrafficBlocks(String pathToCsvFile, LocalDate inputDate) {
        int numberOfTrafficBlocks = 0;

        try (FileInputStream inputStream = new FileInputStream(pathToCsvFile);
             Scanner scanner = new Scanner(inputStream, "UTF-8")) {

            DateTimeFormatter trafficBlockDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");

            int numberOfLinesRead = 0;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                numberOfLinesRead++;

                // Ignore csv file header
                if (numberOfLinesRead > 1) {
                    String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                    LocalDate trafficBlockStartDate = LocalDate.parse(values[10], trafficBlockDateFormat);
                    LocalDate trafficBlockEndDate = LocalDate.parse(values[11], trafficBlockDateFormat);

                    if (inputDate.isAfter(trafficBlockStartDate) && inputDate.isBefore(trafficBlockEndDate)) {
                        numberOfTrafficBlocks++;
                    }
                }
            }

            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }

        return numberOfTrafficBlocks;
    }
}
