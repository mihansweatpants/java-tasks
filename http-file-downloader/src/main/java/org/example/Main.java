package org.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
//    public static String WRONG_NUM_OF_ARGUMENTS_ERR_MSG = "Need to specify path to file with URLs and directory to save files to";

    private static final int BUFFER_SIZE = 64 * 1024;

    public static void main(String[] args) {
//        if (args.length < 2) {
//            throw new IllegalArgumentException(WRONG_NUM_OF_ARGUMENTS_ERR_MSG);
//        }
//
//        String pathToURLsFile = args[0];
//        String saveDirectory = args[1];

        var fileUrl = "https://raw.githubusercontent.com/mihansweatpants/java-tasks/master/README.md";

        try {
            downloadFile(fileUrl, "test-save-directory");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadFile(String fileURL, String saveDirectory) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        int responseCode = httpConnection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
            String saveFilePath = String.format("%s%s%s", saveDirectory, File.separator, fileName);

            InputStream inputStream = httpConnection.getInputStream();
            OutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read()) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            System.out.printf("File %s successfully downloaded to %s\n", fileName, saveDirectory);
        }
        else {
            System.out.printf("Couldn't download file %s. HTTP Status: %s\n", fileURL, responseCode);
        }
    }
}
