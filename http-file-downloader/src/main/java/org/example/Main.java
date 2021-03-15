package org.example;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        Input input = parseArguments(args);

        try (Stream<String> lines = Files.lines(Path.of(input.getPathToURLsFile()))) {
            lines
                    .distinct()
                    .parallel()
                    .forEach(fileURL -> downloadFile(fileURL, input.getSaveDirectory()));
        } catch (IOException e) {
            throw new RuntimeException("Error reading URLs file", e);
        }
    }

    public static void downloadFile(String fileURL, String saveDirectory) {
        try {
            URL url = new URL(fileURL);
            String fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);

            try (InputStream inputStream = url.openStream()) {
                Files.copy(inputStream, Path.of(saveDirectory, fileName), StandardCopyOption.REPLACE_EXISTING);
                System.out.printf("Successfully downloaded file %s to %s\n", fileName, saveDirectory);
            }
        } catch (IOException e) {
            System.out.printf("Couldn't download from %s. Reason: %s\n", fileURL, e.getMessage());
        }
    }

    private static Input parseArguments(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Need to specify path to file with URLs and directory to save files to");
        }

        String pathToURLsFile = args[0];
        String saveDirectory = args[1];

        return new Input(pathToURLsFile, saveDirectory);
    }
}
