package de.lino.entity;

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

}
