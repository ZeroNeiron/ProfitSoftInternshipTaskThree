package org.example.service;

public interface Writer extends AutoCloseable {
    void writeToFile(String line);
}
