package ca.jrvs.apps.jdbc;

import java.util.Optional;

/**
 * Generic interface for CRUD operations.
 * @param <T> The type of the entity.
 * @param <ID> The type of the entity's identifier.
 */
public interface CrudDao<T, ID> {

    /**
     * Saves a given entity. Used for create and update operations.
     * @param entity The entity to be saved. Must not be null.
     * @return The saved entity. Will never be null.
     * @throws IllegalArgumentException If the entity or its ID is null.
     */
    T save(T entity) throws IllegalArgumentException;

    /**
     * Retrieves an entity by its ID.
     * @param id The ID of the entity to retrieve. Must not be null.
     * @return An Optional containing the entity with the given ID, or an empty Optional if none found.
     * @throws IllegalArgumentException If the ID is null.
     */
    Optional<T> findById(ID id) throws IllegalArgumentException;

    /**
     * Retrieves all entities.
     * @return An Iterable containing all entities.
     */
    Iterable<T> findAll();

    /**
     * Deletes the entity with the given ID. If the entity is not found, it is silently ignored.
     * @param id The ID of the entity to delete. Must not be null.
     * @throws IllegalArgumentException If the ID is null.
     */
    void deleteById(ID id) throws IllegalArgumentException;

    /**
     * Deletes all entities managed by the repository.
     */
    void deleteAll();

    /**
     * Checks if an entity with the given ID exists.
     * @param id The ID of the entity to check. Must not be null.
     * @return true if an entity with the given ID exists, false otherwise.
     * @throws IllegalArgumentException If the ID is null.
     */
    boolean exists(ID id) throws IllegalArgumentException;
}
