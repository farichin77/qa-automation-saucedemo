package com.saucedemo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    private static Properties properties;

    static {
        try {
            properties = new Properties();
            FileInputStream file = new FileInputStream("src/test/resources/config.properties");
            properties.load(file);
            file.close();
        } catch (IOException e) {
            logger.error("Error reading config file: {}", e.getMessage());
            throw new RuntimeException("Failed to load config properties");
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Property '{}' not found in config file", key);
        }
        return value;
    }
}
