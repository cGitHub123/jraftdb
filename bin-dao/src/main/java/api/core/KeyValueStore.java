package api.core;

import api.core.exception.DeleteAllFailedException;
import api.core.exception.DeleteFailedException;
import api.core.exception.FindFailedException;
import api.core.exception.SaveFailedException;
import api.mapper.exception.DeserializationException;
import api.mapper.exception.SerDeException;
import api.mapper.exception.SerializationException;

import java.util.Collection;
import java.util.Optional;

/**
 * author caibin
 * date 2021-07-15
 */
public interface KeyValueStore<K, V> {

    /**
     * Inserts key-value pair into RocksDB.
     *
     * @param key of value.
     * @param value that should be persisted.
     * @throws SerializationException when it's not possible to serialize entity.
     * @throws SaveFailedException when it's not possible to persist entity.
     */
    void save(K key, V value) throws SerializationException, SaveFailedException;

    /**
     * Try to find value for a given key.
     *
     * @param key of entity that should be retrieved.
     * @return Optional of entity.
     * @throws SerDeException when it's not possible to serialize/deserialize entity.
     * @throws FindFailedException when it's not possible to retrieve a wanted entity.
     */
    Optional<V> findByKey(K key) throws SerDeException, FindFailedException;

    /**
     * Try to find all entities from repository.
     *
     * @return Collection of entities.
     * @throws DeserializationException when it's not possible to deserialize entity.
     */
    Collection<V> findAll() throws DeserializationException;

    /**
     * Delete entity for a given key.
     *
     * @param key of entity that should be deleted.
     * @throws SerializationException when it's not possible to serialize entity.
     * @throws DeleteFailedException when it's not possible to delete a wanted entity.
     */
    void deleteByKey(K key) throws SerializationException, DeleteFailedException;

    /**
     * Deletes all entities from RocksDB.
     *
     * @throws DeleteAllFailedException when it's not possible to delete all entities.
     */
    void deleteAll() throws DeleteAllFailedException;
}
