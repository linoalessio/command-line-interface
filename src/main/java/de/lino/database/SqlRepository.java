package de.lino.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;
import de.lino.database.config.Credentials;
import de.lino.database.config.HikariDatabaseConfiguration;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Class creates a database connection using HikariClient improving its data handling
 *
 * @apiNote HikariDataSource handles multiple database connection efficiently and security content.
 *          AutoCommit turned off due to security reasons, database rolls back if statement fails
 */
public class SqlRepository {

    /**
     * HikariDataSource class used for managing database connection efficiently
     */
    private HikariDataSource hikariDataSource;

    /**
     * Using AtomicBoolean to check if connecting was successful
     * <p>Avoid Race Condition using Atomics</p>
     */
    private final AtomicBoolean connected = new AtomicBoolean(false);
    
    /**
     * Setting up HikariClient and performance properties and opening database connection efficiently
     *
     * @param credentials Database credentials for authentification
     */
    public SqlRepository(final HikariDatabaseConfiguration hikariDatabaseConfiguration, final Credentials credentials) {

    	try {
    	
            final HikariConfig hikariConfig = new HikariConfig();

            hikariConfig.setMaximumPoolSize(10);
            hikariConfig.setMinimumIdle(1);
            hikariConfig.setConnectionTimeout(10_000);
            hikariConfig.setIdleTimeout(10_000);
            hikariConfig.setAutoCommit(true);

            hikariConfig.setMaxLifetime(TimeUnit.SECONDS.toMillis(30));
            hikariConfig.setKeepaliveTime(TimeUnit.SECONDS.toMillis(5));

            hikariConfig.setDriverClassName(hikariDatabaseConfiguration.jdbc());
            hikariConfig.setJdbcUrl(
                    String.format(
                            hikariDatabaseConfiguration.repositoryLink(), credentials.getAddress(), credentials.getPort(), credentials.getDatabase()
                    )
            );

            hikariConfig.setUsername(credentials.getUsername());
            hikariConfig.setPassword(credentials.getPassword());

            if (!hikariDatabaseConfiguration.properties().isEmpty()) hikariDatabaseConfiguration.properties().forEach(hikariConfig::addDataSourceProperty);

            this.hikariDataSource = new HikariDataSource(hikariConfig);
    		this.connected.set(true);
            
    	} catch (final PoolInitializationException exception) {}

    }

    /**
     * Import SQL script from target path
     * @param scritPath Path where SQL-Script is found
     */
    public void importScript(final Path scritPath) {

        try {

            final ScriptRunner scriptRunner = new ScriptRunner(this.hikariDataSource.getConnection());
            scriptRunner.setStopOnError(true);
            scriptRunner.setAutoCommit(true);
            scriptRunner.setSendFullScript(false);
            scriptRunner.setDelimiter(";");

            try (final Reader reader = Files.newBufferedReader(scritPath)) {
                scriptRunner.runScript(reader);
            }

        } catch (final SQLException | IOException exception) {
            Logger.getAnonymousLogger().severe("@importScript: " + exception.getMessage());
        }

    }

    /**
     * Shutdown database connection
     */
    public void shutdown() {
        if (!this.isConnected().get()) return;
        this.hikariDataSource.close();
        Logger.getAnonymousLogger().info("SqlRepository has been shut down.");
    }

    /**
     * Execute update in database with n entries in query statement and assuring rollback call if query fails
     *
     * @param query SqlQuery statement for execution
     * @param entries Objects to be inserted
     *
     * @apiNote sample: {@code this.execute("INSERT ? INTO ? WHERE Id=?", 300, "table", 1);}
     */
    public SqlRepository execute(String query, Object... entries) {

        try (final Connection connection = this.hikariDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            connection.setAutoCommit(false);
            this.callProcedure(preparedStatement, entries);

            preparedStatement.executeUpdate();
            connection.commit();

        } catch (final SQLException sqlException) {
            Logger.getAnonymousLogger().severe("@execute.execution: " + sqlException.getMessage());

            try (final Connection connection = this.hikariDataSource.getConnection()) {
                if (!connection.getAutoCommit()) connection.rollback();
            } catch (final SQLException rollBackException) {
                Logger.getAnonymousLogger().severe("@execute.rollback: " + rollBackException.getMessage());
            }

        }

        return this;
    }

    /**
     * Execute query in database with n entries in query statement
     *
     * @param query SqlQuery statement for database
     * @param resultMapping Function applying ResultSet and value T
     * @param defaultResult Default Result if no match appears
     * @param entries Objects for query
     * @param <T> Generic object to be returned
     * @return matched query result or default value
     *
     * @apiNote sample: {@code this.request("SELECT * FROM table", result -> {}, null);}
     */
    public <T> T request(String query, SqlMapping<ResultSet, T> resultMapping, T defaultResult, Object... entries) {

        try (final Connection connection = this.hikariDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            this.callProcedure(preparedStatement, entries);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultMapping.apply(resultSet);
            }

        } catch (final SQLException sqlException) {
            Logger.getAnonymousLogger().severe("@request: " + sqlException.getMessage());
            return defaultResult;
        }

    }

    /**
     * Calling procedure to set entries in database in order
     *
     * @param preparedStatement PreparedStatement that inserts objects
     * @param entries Objects to be inserted
     * @throws SQLException called if run failed
     */
    private void callProcedure( PreparedStatement preparedStatement, Object... entries) throws SQLException {
        for (int i = 0; i < entries.length; i++)
            preparedStatement.setObject(i + 1, entries[i]);
    }

    /**
     * Check if connection to database was created
     * @return Atomic object
     */
	public AtomicBoolean isConnected() {
		return connected;
	}

}
