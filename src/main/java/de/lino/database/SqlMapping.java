package de.lino.database;

import java.util.function.Function;

/**
 * SqlMapping applies an object to the interface
 *
 * @param <T> Key object
 * @param <V> Value object
 */
public interface SqlMapping<T, V> extends Function<T, V> {}

