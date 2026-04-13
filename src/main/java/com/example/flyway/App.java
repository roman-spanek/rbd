package com.example.flyway;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Entry point for the Flyway PostgreSQL demo application.
 *
 * <p>Database connection settings are read from {@code application.properties}
 * on the classpath. Individual values can be overridden at runtime via
 * system properties (higher priority) or environment variables (medium
 * priority); the properties file provides the baseline defaults.
 *
 * <p>Property keys in {@code application.properties}:
 * <ul>
 *   <li>{@code db.url}      – JDBC URL, e.g. {@code jdbc:postgresql://localhost:5432/mydb}</li>
 *   <li>{@code db.user}     – database username</li>
 *   <li>{@code db.password} – database password</li>
 * </ul>
 *
 * <p>Override precedence (highest → lowest):
 * <ol>
 *   <li>JVM system property (e.g. {@code -Ddb.url=...})</li>
 *   <li>Environment variable using the upper-snake-case key (e.g. {@code DB_URL})</li>
 *   <li>Value in {@code application.properties}</li>
 * </ol>
 */
public class App {

    static final String PROPERTIES_FILE = "application.properties";

    public static void main(String[] args) {
        Properties props = loadProperties(PROPERTIES_FILE);

        String url      = resolve("db.url",      "DB_URL",      props);
        String user     = resolve("db.user",     "DB_USER",     props);
        String password = resolve("db.password", "DB_PASSWORD", props);

        System.out.println("Connecting to: " + url);
        System.out.println("User          : " + user);

        Flyway flyway = Flyway.configure()
                .dataSource(url, user, password)
                .locations("classpath:db/migration")
                .load();

        // Print current migration state before running
        MigrationInfoService infoService = flyway.info();
        System.out.println("\n--- Migration status BEFORE ---");
        printMigrationInfo(infoService);

        // Apply all pending migrations
        int applied = flyway.migrate().migrationsExecuted;
        System.out.println("\nMigrations applied: " + applied);

        // Print final migration state
        System.out.println("\n--- Migration status AFTER ---");
        printMigrationInfo(flyway.info());
    }

    /**
     * Loads a {@link Properties} file from the classpath.
     *
     * @param filename classpath-relative filename
     * @return loaded properties (may be empty if the file is not found)
     */
    static Properties loadProperties(String filename) {
        Properties props = new Properties();
        try (InputStream in = App.class.getClassLoader().getResourceAsStream(filename)) {
            if (in != null) {
                props.load(in);
            } else {
                System.err.println("Warning: " + filename + " not found on classpath; using defaults.");
            }
        } catch (IOException e) {
            System.err.println("Warning: could not read " + filename + ": " + e.getMessage());
        }
        return props;
    }

    /**
     * Resolves a configuration value using the following precedence:
     * <ol>
     *   <li>JVM system property ({@code propKey})</li>
     *   <li>Environment variable ({@code envKey})</li>
     *   <li>Value in the supplied {@link Properties}</li>
     *   <li>Empty string</li>
     * </ol>
     */
    static String resolve(String propKey, String envKey, Properties fileProps) {
        String value = System.getProperty(propKey);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        value = System.getenv(envKey);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        return fileProps.getProperty(propKey, "");
    }

    private static void printMigrationInfo(MigrationInfoService service) {
        MigrationInfo[] migrations = service.all();
        if (migrations == null || migrations.length == 0) {
            System.out.println("  (no migrations found)");
            return;
        }
        System.out.printf("  %-10s %-40s %-10s%n", "Version", "Description", "State");
        System.out.println("  " + "-".repeat(62));
        for (MigrationInfo info : migrations) {
            System.out.printf("  %-10s %-40s %-10s%n",
                    info.getVersion() != null ? info.getVersion().toString() : "<<",
                    info.getDescription(),
                    info.getState().name());
        }
    }
}