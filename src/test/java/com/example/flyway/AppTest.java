package com.example.flyway;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link App} helper methods.
 * These tests do not require a running database.
 */
class AppTest {

    // -----------------------------------------------------------------------
    // loadProperties
    // -----------------------------------------------------------------------

    @Test
    void loadProperties_loadsValuesFromClasspath() {
        Properties props = App.loadProperties(App.PROPERTIES_FILE);
        // The file must be present on the test classpath
        assertFalse(props.isEmpty(), "application.properties should not be empty");
        assertNotNull(props.getProperty("db.url"),  "db.url must be present");
        assertNotNull(props.getProperty("db.user"), "db.user must be present");
    }

    @Test
    void loadProperties_returnsEmptyPropertiesForMissingFile() {
        Properties props = App.loadProperties("nonexistent-file.properties");
        assertTrue(props.isEmpty(), "Missing file should yield empty Properties");
    }

    // -----------------------------------------------------------------------
    // resolve
    // -----------------------------------------------------------------------

    @Test
    void resolve_returnsFileValueWhenNoOverride() {
        Properties props = new Properties();
        props.setProperty("db.url", "jdbc:postgresql://file-host:5432/db");

        String result = App.resolve("db.url", "DB_URL", props);
        assertEquals("jdbc:postgresql://file-host:5432/db", result);
    }

    @Test
    void resolve_systemPropertyOverridesFileValue() {
        Properties props = new Properties();
        props.setProperty("db.url", "jdbc:postgresql://file-host:5432/db");

        System.setProperty("db.url", "jdbc:postgresql://sysprop-host:5432/db");
        try {
            String result = App.resolve("db.url", "DB_URL", props);
            assertEquals("jdbc:postgresql://sysprop-host:5432/db", result);
        } finally {
            System.clearProperty("db.url");
        }
    }

    @Test
    void resolve_returnsEmptyStringWhenNothingSet() {
        Properties props = new Properties();
        String result = App.resolve("__no_such_key__", "__NO_SUCH_ENV__", props);
        assertEquals("", result);
    }
}