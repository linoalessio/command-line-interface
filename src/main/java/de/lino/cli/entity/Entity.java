package de.lino.cli.entity;

import java.io.Serializable;

/**
 * Entity interface containing generic data for
 */
public interface Entity extends Serializable {

    /**
     * Entity properties formatted to readable style
     * @return Formatted entity object
     */
    String toString();

    /**
     * Creating a hash for storing entity in map
     * @apiNote {@code Entity().toString().hashCode()}
     * @return Hashcode of encrypted entity
     */
    int hashCode();

}
