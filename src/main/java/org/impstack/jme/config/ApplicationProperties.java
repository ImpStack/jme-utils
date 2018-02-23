package org.impstack.jme.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum ApplicationProperties {
    INSTANCE;

    private final Logger LOG = LoggerFactory.getLogger(ApplicationProperties.class);
    private final Properties properties;

    ApplicationProperties() {
        properties = new Properties();
        try (InputStream in = getClass().getResourceAsStream("/application.properties")) {
            properties.load(in);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public String get(String key, String fallback) {
        return properties.getProperty(key, fallback);
    }

    public boolean get(String key, boolean fallback) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(fallback)));
    }

    public int get(String key, int fallback) {
        return Integer.parseInt(properties.getProperty(key, String.valueOf(fallback)));
    }

}
