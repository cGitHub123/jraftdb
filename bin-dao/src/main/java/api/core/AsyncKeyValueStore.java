package api.core;

import api.core.exception.DeleteAllFailedException;
import api.core.exception.DeleteFailedException;
import api.core.exception.FindFailedException;
import api.core.exception.SaveFailedException;
import api.mapper.exception.SerDeException;
import api.mapper.exception.SerializationException;
import api.mapper.exception.DeserializationException;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * author caibin
 * date 2021-07-15
 */
public interface AsyncKeyValueStore<K, V> {

    /**
     * Inserts key-value pair into RocksDB asynchronously.
     *
     * @param key of value.
     * @param value that should be persisted.
     * @return CompletableFuture of Void.
     * @throws SerializationException when it's not possible to serialize entity.
     * @throws SaveFailedException when it's not possible to persist entity.
     */
    CompletionStage<Void> save(K key, V value) throws SerializationException, SaveFailedException;

    /**
     * Try to find value for a given key asynchronously.
     *
     * @param key of entity that should be retrieved.
     * @return CompletableFuture of Optional of entity.
     * @throws SerDeException when it's not possible to serialize/deserialize entity.
     * @throws FindFailedException when it's not possible to retrieve a wanted entity.
     */
    CompletionStage<Optional<V>> findByKey(K key) throws SerDeException, FindFailedException;

    /**
     * Try to find all entities from repository asynchronously.
     *
     * @return CompletableFuture of Collection of entities.
     * @throws DeserializationException when it's not possible to deserialize entity.
     */
    CompletionStage<Collection<V>> findAll() throws DeserializationException;

    /**
     * Delete entity for a given key asynchronously..
     *
     * @param key of entity that should be deleted.
     * @return CompletableFuture of Void.
     * @throws SerializationException when it's not possible to serialize entity.
     * @throws DeleteFailedException when it's not possible to delete a wanted entity.
     */
    CompletionStage<Void> deleteByKey(K key) throws SerializationException, DeleteFailedException;

    /**
     * Deletes all entities from RocksDB asynchronously.
     *
     * @return CompletableFuture of Void.
     * @throws DeleteAllFailedException when it's not possible to delete entity.
     */
    CompletionStage<Void> deleteAll() throws DeleteAllFailedException;
}
