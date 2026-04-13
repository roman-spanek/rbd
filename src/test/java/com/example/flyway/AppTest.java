package com.example.flyway;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link App} helper methods.
 * These tests do not require a running database.
 */
class AppTest {

    @Test
    void getConfig_returnsDefaultWhenNothingSet() {
        // Use a key that is almost certainly not set in the environment
        String result = App.getConfig("__UNLIKELY_KEY_XYZ__", "default-value");
        assertEquals("default-value", result);
    }

    @Test
    void getConfig_returnsSystemPropertyOverDefault() {
        System.setProperty("TEST_CONFIG_KEY", "from-system-property");
        try {
            String result = App.getConfig("TEST_CONFIG_KEY", "default-value");
            assertEquals("from-system-property", result);
        } finally {
            System.clearProperty("TEST_CONFIG_KEY");
        }
    }

    @Test
    void getConfig_treatsEmptySystemPropertyAsUnset() {
        System.setProperty("TEST_CONFIG_EMPTY", "");
        try {
            String result = App.getConfig("TEST_CONFIG_EMPTY", "fallback");
            assertEquals("fallback", result);
        } finally {
            System.clearProperty("TEST_CONFIG_EMPTY");
        }
    }
}
