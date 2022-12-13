package org.example.util;

import java.time.Instant;

public class PropertyObject {
    @Property(name = "stringProperty")
  private String stringProperty;

    @Property(name = "numberProperty")
  private int numberProperty;
    @Property(name = "timeProperty", format = "dd.MM.yyyy mm:ss")
  private Instant timeProperty;

    public String getStringProperty() {
        return stringProperty;
    }

    public int getNumberProperty() {
        return numberProperty;
    }

    public Instant getTimeProperty() {
        return timeProperty;
    }
}
