package org.example;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Need to specify path to csv file and date (dd.MM.yyyy)");
        }

        String pathToCsvFile = args[0];
        LocalDate inputDate = LocalDate.parse(args[1], DateTimeFormatter.ofPattern("dd.MM.yyyy"));

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

        System.out.printf("There were %d traffic blocks in %s", numberOfTrafficBlocks, args[1]);
    }
}
