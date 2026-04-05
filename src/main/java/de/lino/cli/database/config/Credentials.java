package de.lino.cli.database.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Class containing database credentials and automatically creating PROPERTIES file
 * {@link java.util.Properties}
 */
public class Credentials {

    /**
     * Credentials for database connection
     */
    private String address, database, username, password;

    /**
     * Credentials for database connection
     */
    private int port;

    /**
     * Constructor initializing database credentials for successful connection and handling database.properties
     * @param address DNS- or IP-Address for connection
     * @param database Database name
     * @param username Username for connection
     * @param password Password to verify identity
     * @param port Port for connection to proper server
     */
	public Credentials(String address, String database, String username, String password, int port) {
		this.address = address;
		this.database = database;
		this.username = username;
		this.password = password;
		this.port = port;
	}
    
	/**
     * Build credentials from properties file if exists, otherwise will be created and stored with credentials
     *
     * @param path Path to properties file
     */
    public void build(final Path path) {

        if (!path.toString().endsWith(".properties"))
            throw  new RuntimeException("@build: Provided path does not lead to '.properties' file");

        final File resultPath = Paths.get(path.toString()).toFile();

        if (Files.notExists(path)) {

            final Properties properties = new Properties();
            properties.put("address", this.address);
            properties.put("database", this.database);
            properties.put("username", this.username);
            properties.put("password", this.password);
            properties.put("port", String.valueOf(this.port));

            try {
                if (path.getParent() != null)
                    Files.createDirectories(path.getParent());
            } catch (final IOException exception) {
                Logger.getAnonymousLogger().severe("@Credentials.build.directories: " + exception.getMessage());
            }

            try (final FileOutputStream stream = new FileOutputStream(resultPath)) {
                properties.store(stream, "database credentials");
            } catch (final IOException exception) {
                Logger.getAnonymousLogger().severe("@Credentials.build.create: " + exception.getMessage());
            }

            return;
        }

        try (final FileInputStream stream = new FileInputStream(resultPath)) {

            final Properties properties = new Properties();
            properties.load(stream);

            this.address = properties.getProperty("address");
            this.database = properties.getProperty("database");
            this.username = properties.getProperty("username");
            this.password = properties.getProperty("password");
            this.port = Integer.parseInt(properties.getProperty("port"));

        } catch (final IOException exception) {
            Logger.getAnonymousLogger().severe("@Credentials.build.cache: " + exception.getMessage());
        }

    }

    /**
     * Get address of configuration
     * @return address
     */
    public String getAddress() {
		return address;
	}

    /**
     * Get database  of configuration
     * @return database
     */
	public String getDatabase() {
		return database;
	}

    /**
     * Get username of configuration
     * @return username
     */
	public String getUsername() {
		return username;
	}

    /**
     * Get password of configuration
     * @return password
     */
	public String getPassword() {
		return password;
	}

    /**
     * Get port of configuration
     * @return port
     */
	public int getPort() {
		return port;
	}

}
