package org.example;

public class Input {
    private final String pathToURLsFile;
    private final String saveDirectory;

    public Input(String pathToURLsFile, String saveDirectory) {
        this.pathToURLsFile = pathToURLsFile;
        this.saveDirectory = saveDirectory;
    }

    public String getPathToURLsFile() {
        return pathToURLsFile;
    }

    public String getSaveDirectory() {
        return saveDirectory;
    }
}
