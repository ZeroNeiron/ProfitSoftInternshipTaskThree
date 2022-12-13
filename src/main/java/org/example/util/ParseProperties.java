package org.example.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Properties;

public class ParseProperties {

    public static <T> T loadFromProperties(Class<T> cls, Path propertiesPath) throws Exception {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(String.valueOf(propertiesPath))) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Can`t open file ", e);
        }
        Constructor<T> constructor = cls.getConstructor();
        T t = constructor.newInstance();

        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(Property.class)) {
                    Property property = (Property) annotation;
                    setParams(properties, property, t, field);
                }
            }
        }
        return t;
    }

    private static <T> void setParams(Properties properties, Property property, T t, Field field)
            throws IllegalAccessException {

        String s = properties.getProperty(property.name());
        switch (field.getType().getName()) {
            case "java.lang.String":
                field.set(t, s);
                break;
            case "int":
                field.set(t, Integer.parseInt(s));
                break;
            case "java.time.Instant":
                try {
                    SimpleDateFormat format = new SimpleDateFormat(property.format());
                    Instant instant = format.parse(s).toInstant();
                    field.set(t, instant);
                } catch (Exception e) {
                    System.err.println("Wrong format");
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
