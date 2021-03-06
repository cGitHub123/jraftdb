package api.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import api.core.exception.DeleteAllFailedException;
import api.core.exception.DeleteFailedException;
import api.core.exception.FindFailedException;
import api.core.exception.SaveFailedException;
import api.mapper.Mapper;
import api.mapper.exception.DeserializationException;
import api.mapper.exception.SerDeException;
import api.mapper.exception.SerializationException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author caibin
 * date 2021-07-15
 */
public abstract class AsyncKVStore<K, V> implements AsyncKeyValueStore<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncKVStore.class);

    private final KVStore<K, V> underlying;
    private final ExecutorService executorService;

    /**
     * Default constructor which automatically infers key and value types needed for mapper creation.
     *
     * @param configuration for {@link RocksDBConnection}.
     */
    public AsyncKVStore(final RocksDBConfiguration configuration) {
        this.underlying = new KVStore<>(configuration, extractKeyType(), extractValueType());
        this.executorService = Executors.newFixedThreadPool(configuration.threadCount());
    }

    /**
     *
     * @param configuration for {@link RocksDBConnection}.
     * @param keyType for mapper.
     * @param valueType for mapper.
     */
    public AsyncKVStore(
            final RocksDBConfiguration configuration,
            final Class<K> keyType,
            final Class<V> valueType
    ) {
        this.underlying = new KVStore<>(configuration, keyType, valueType);
        this.executorService = Executors.newFixedThreadPool(configuration.threadCount());
    }

    /**
     *
     * @param configuration for {@link RocksDBConnection}.
     * @param keyMapper custom key mapper that implements {@link Mapper}.
     * @param valueMapper custom value mapper that implements {@link Mapper}.
     */
    public AsyncKVStore(
            final RocksDBConfiguration configuration,
            final Mapper<K> keyMapper,
            final Mapper<V> valueMapper
    ) {
        this.underlying = new KVStore<>(configuration, keyMapper, valueMapper);
        this.executorService = Executors.newFixedThreadPool(configuration.threadCount());
    }

    @Override
    public CompletableFuture<Void> save(
            final K key,
            final V value
    ) {
        final CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
                try {
                    underlying.save(key, value);
                    future.complete(null);
                } catch (final SerializationException | SaveFailedException exception) {
                    future.completeExceptionally(exception);
                }
            }, executorService);

        return future;
    }

    @Override
    public CompletableFuture<Optional<V>> findByKey(final K key) {
        final CompletableFuture<Optional<V>> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                future.complete(underlying.findByKey(key));
            } catch (final SerDeException | FindFailedException exception) {
                future.completeExceptionally(exception);
            }
        }, executorService);

        return future;
    }

    @Override
    public CompletableFuture<Collection<V>> findAll() {
        final CompletableFuture<Collection<V>> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                future.complete(underlying.findAll());
            } catch (final DeserializationException exception) {
                future.completeExceptionally(exception);
            }
        }, executorService);

        return future;
    }

    @Override
    public CompletableFuture<Void> deleteByKey(final K key) {
        final CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                underlying.deleteByKey(key);
                future.complete(null);
            } catch (final SerializationException | DeleteFailedException exception) {
                future.completeExceptionally(exception);
            }
        }, executorService);

        return future;
    }

    @Override
    public CompletableFuture<Void> deleteAll() {
        final CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                underlying.deleteAll();
                future.complete(null);
            } catch (final DeleteAllFailedException exception) {
                future.completeExceptionally(exception);
            }
        }, executorService);

        return future;
    }

    @SuppressWarnings("unchecked")
    private Class<K> extractKeyType() {
        return (Class<K>) extractClass(((ParameterizedType) getGenericSuperClass()).getActualTypeArguments()[0]);
    }

    @SuppressWarnings("unchecked")
    private Class<V> extractValueType() {
        return (Class<V>) extractClass(((ParameterizedType) getGenericSuperClass()).getActualTypeArguments()[1]);
    }

    private Type getGenericSuperClass() {
        final Type superClass = getClass().getGenericSuperclass();

        if (superClass instanceof Class<?>) {
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        }

        return superClass;
    }

    private Class<?> extractClass(final Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }

        throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
    }
}
