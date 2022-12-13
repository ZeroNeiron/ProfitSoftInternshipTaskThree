package org.example.service;

public interface Reader extends AutoCloseable {
    String readLine();
}
