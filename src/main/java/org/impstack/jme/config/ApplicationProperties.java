package org.impstack.jme.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * A singleton helper implementation that loads a properties file and exposes some methods to retrieve type casted
 * values from the file.
 * <p>
 * This class will load the file specified in the 'application.settings' property. When this property isn't found, it
 * will try to load an application.properties file in the root of the classpath.
 * </p>
 * Example:
 * This will try to find the file '/home/user/custom.properties' on the filesystem.
 * -Dapplication.settings=/home/user/custom.properties
 */
public enum ApplicationProperties {
    INSTANCE;

    private final String ENV_APPLICATION_FILE = "application.settings";
    private final String DEFAULT_FILE = "/application.properties";
    private final Logger LOG = LoggerFactory.getLogger(ApplicationProperties.class);
    private final Properties properties;

    ApplicationProperties() {
        properties = new Properties();
        String environmentFile = System.getProperty(ENV_APPLICATION_FILE);
        if (environmentFile != null) {
            LOG.info("Found {} property. Loading {}", ENV_APPLICATION_FILE, environmentFile);
        } else {
            LOG.info("No {} property found, loading default {}", ENV_APPLICATION_FILE, DEFAULT_FILE);
        }
        try (InputStream in = environmentFile != null ? Files.newInputStream(Paths.get(environmentFile)) : getClass().getResourceAsStream(DEFAULT_FILE)) {
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
