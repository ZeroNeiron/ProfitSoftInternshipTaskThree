package org.example.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.example.service.Reader;

public class XmlFileReader implements Reader {
    private String delimiter;
    private final String inputFileName;
    private Scanner scanner;
    private FileInputStream fileInputStream;

    public XmlFileReader(String inputFileName, String delimiter) {
        this.delimiter = delimiter;
        this.inputFileName = inputFileName;
        createScanner();
    }

    @Override
    public String readLine() {
        if (scanner.hasNext()) {
            return scanner.next();
        }
        scanner.close();

        return "";
    }

    private void createScanner() {
        try {
            this.fileInputStream = new FileInputStream(inputFileName);
            this.scanner = new Scanner(fileInputStream,
                    StandardCharsets.UTF_8).useDelimiter(delimiter);
        } catch (IOException e) {
            throw new RuntimeException("Can`t read data from file", e);
        }
    }

    @Override
    public void close() {
        try {
            scanner.close();
            fileInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
