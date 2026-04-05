package de.lino.cli.setup.status;

import java.util.Arrays;

/**
 * Setup status enum for managing setup status easily
 */
public enum SetupStatus {

    SLEEPING("Sleeping"),
    RUNNING("Running"),
    FINISHED("Finished");

    /**
     * String formatted name of status
     */
    private final String name;

    /**
     * Constructor for initializing properties of status object
     * @param name name of status object
     */
    SetupStatus(final String name) {
        this.name = name;
    }

    /**
     * Return formatted name of status
     * @return formatted name
     */
    public String getName() {
        return name;
    }

    /**
     * Get enum type status from string
     * @param name String formatted status type
     * @return SetupStatus
     */
    public static SetupStatus fromString(final String name) {
        return Arrays.stream(SetupStatus.values()).filter(entry -> entry.name.equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
