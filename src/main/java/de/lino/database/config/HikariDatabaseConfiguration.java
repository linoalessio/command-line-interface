package de.lino.database.config;

import java.util.Map;

/**
 * Hikari configuration for database management
 * @param properties Map<String, String> containing properties to set up the hikari client
 * @param jdbc JDBC class path for database connection
 * @param repositoryLink Database string formatted link for connecting ({@code jdbc:postgresql://%s:%d/%s?serverTimezone=UTC})
 */
public record HikariDatabaseConfiguration(Map<String, String> properties, String jdbc, String repositoryLink) { }