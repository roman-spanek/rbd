package com.example.flyway;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;

/**
 * Entry point for the Flyway PostgreSQL demo application.
 *
 * <p>Reads database connection details from environment variables or system
 * properties, then runs all pending Flyway migrations found under
 * {@code src/main/resources/db/migration}.
 *
 * <p>Environment variables / system properties:
 * <ul>
 *   <li>{@code DB_URL}      – JDBC URL, e.g. {@code jdbc:postgresql://localhost:5432/mydb}</li>
 *   <li>{@code DB_USER}     – database username (default: {@code postgres})</li>
 *   <li>{@code DB_PASSWORD} – database password (default: empty string)</li>
 * </ul>
 */
public class App {

    public static void main(String[] args) {
        String url      = getConfig("DB_URL",      "jdbc:postgresql://localhost:5432/mydb");
        String user     = getConfig("DB_USER",     "postgres");
        String password = getConfig("DB_PASSWORD", "");

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
     * Reads a value first from system properties, then from environment
     * variables, falling back to {@code defaultValue}.
     */
    static String getConfig(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null || value.isEmpty()) {
            value = System.getenv(key);
        }
        return (value != null && !value.isEmpty()) ? value : defaultValue;
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
