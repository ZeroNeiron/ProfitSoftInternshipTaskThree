package org.example.util;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import org.junit.Test;

public class ParsePropertiesTest {
    Path path = Paths.get("src/main/resources/application.properties");


    @Test
    public void correctlyWorkTest() {

        PropertyObject test = new PropertyObject();
        try {
            test = ParseProperties.loadFromProperties(test.getClass(), path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals("value1", test.getStringProperty());
        assertEquals(10, test.getNumberProperty());
        assertEquals(Instant.parse("2022-11-28T22:18:30Z"), test.getTimeProperty());
    }
}