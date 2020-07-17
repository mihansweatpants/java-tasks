package org.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static final String WRONG_NUM_OF_ARGUMENTS_ERR_MSG = "Need to specify path to file with URLs and directory to save files to";

    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    private static final int FILE_DOWNLOAD_BUFFER_SIZE = 500 * 1024;

    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException(WRONG_NUM_OF_ARGUMENTS_ERR_MSG);
        }

        String pathToURLsFile = args[0];
        String saveDirectory = args[1];

        try (FileInputStream fileInputStream = new FileInputStream(pathToURLsFile);
            Scanner scanner = new Scanner(fileInputStream, StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()) {
                String fileURL = scanner.nextLine();

                executorService.execute(() -> {
                    try {
                        downloadFile(fileURL, saveDirectory);
                    } catch (Exception e) {
                        System.out.printf("Couldn't download from %s. Reason: %s\n", fileURL, e.getMessage());
                    }
                });
            }
        } catch (IOException e) {
            System.out.printf("Couldn't read from file %s. Reason: %s\n", pathToURLsFile, e.getMessage());
        }

        executorService.shutdown();
    }

    public static void downloadFile(String fileURL, String saveDirectory) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        int responseCode = httpConnection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
            String saveFilePath = String.format("%s%s%s", saveDirectory, File.separator, fileName);

            InputStream inputStream = new BufferedInputStream(httpConnection.getInputStream(), FILE_DOWNLOAD_BUFFER_SIZE);
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(saveFilePath), FILE_DOWNLOAD_BUFFER_SIZE);

            int bytesRead;
            while ((bytesRead = inputStream.read()) != -1) {
                outputStream.write(bytesRead);
            }

            inputStream.close();
            outputStream.close();

            System.out.printf("Successfully downloaded file %s to %s\n", fileName, saveDirectory);
        }
        else {
            System.out.printf("Couldn't download from %s. HTTP Status: %s\n", fileURL, responseCode);
        }
    }
}
