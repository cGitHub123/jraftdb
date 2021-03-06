package api.core;


import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import api.core.exception.DeleteAllFailedException;
import api.core.exception.DeleteFailedException;
import api.core.exception.FindFailedException;
import api.core.exception.SaveFailedException;
import api.mapper.Mapper;
import api.mapper.MapperFactory;
import api.mapper.exception.DeserializationException;
import api.mapper.exception.SerDeException;
import api.mapper.exception.SerializationException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * author caibin
 * date 2021-07-15
 */
public class KVStore<K, V> extends RocksDBConnection implements KeyValueStore<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KVStore.class);

    private final Mapper<K> keyMapper;
    private final Mapper<V> valueMapper;

    /**
     * Default constructor which automatically infers key and value types needed for mapper creation.
     *
     * @param configuration for {@link RocksDBConnection}.
     */
    public KVStore(final RocksDBConfiguration configuration) {
        super(configuration);
        this.keyMapper = MapperFactory.createFor(extractKeyType());
        this.valueMapper = MapperFactory.createFor(extractValueType());
    }

    /**
     *
     * @param configuration for {@link RocksDBConnection}.
     * @param keyType for mapper.
     * @param valueType for mapper.
     */
    public KVStore(
            final RocksDBConfiguration configuration,
            final Class<K> keyType,
            final Class<V> valueType
    ) {
        super(configuration);
        this.keyMapper = MapperFactory.createFor(keyType);
        this.valueMapper = MapperFactory.createFor(valueType);
    }

    /**
     *
     * @param configuration for {@link RocksDBConnection}.
     * @param keyMapper custom key mapper that implements {@link Mapper}.
     * @param valueMapper custom value mapper that implements {@link Mapper}.
     */
    public KVStore(
            final RocksDBConfiguration configuration,
            final Mapper<K> keyMapper,
            final Mapper<V> valueMapper
    ) {
        super(configuration);
        this.keyMapper = keyMapper;
        this.valueMapper = valueMapper;
    }

    @Override
    public void save(
            final K key,
            final V value
    ) throws SerializationException, SaveFailedException {
        try {
            final byte[] serializedKey = keyMapper.serialize(key);
            final byte[] serializedValue = valueMapper.serialize(value);
            rocksDB.put(serializedKey, serializedValue);
        } catch (final SerializationException exception) {
            LOGGER.error("Serialization exception occurred during save operation. {}", exception.getMessage());
            throw exception;
        } catch (final RocksDBException exception) {
            LOGGER.error("RocksDBException occurred during save operation. {}", exception.getMessage());
            throw new SaveFailedException(exception.getMessage(), exception);
        }
    }

    @Override
    public Optional<V> findByKey(final K key) throws SerDeException, FindFailedException {
        try {
            final byte[] serializedKey = keyMapper.serialize(key);
            final byte[] bytes = rocksDB.get(serializedKey);
            return Optional.ofNullable(valueMapper.deserialize(bytes));
        } catch (final SerializationException exception) {
            LOGGER.error("Serialization exception occurred during findByKey operation. {}", exception.getMessage());
            throw exception;
        } catch (final RocksDBException exception) {
            LOGGER.error("RocksDBException occurred during findByKey operation. {}", exception.getMessage());
            throw new FindFailedException(exception.getMessage(), exception);
        } catch (final DeserializationException exception) {
            LOGGER.error("Deserialization exception occurred during findByKey operation. {}", exception.getMessage());
            throw exception;
        }
    }

    @Override
    public Collection<V> findAll() throws DeserializationException {
        try (final RocksIterator iterator = rocksDB.newIterator()) {
            final Collection<V> result = new ArrayList<>();
            iterator.seekToFirst();
            
            while (iterator.isValid()) {
                final V value = valueMapper.deserialize(iterator.value());
                result.add(value);
                iterator.next();
            }

            return result;
        } catch (final DeserializationException exception) {
            LOGGER.error("Deserialization exception occurred during findAll operation. {}", exception.getMessage());
            throw exception;
        }
    }

    @Override
    public void deleteByKey(final K key) throws SerializationException, DeleteFailedException {
        try {
            final byte[] serializedKey = keyMapper.serialize(key);
            rocksDB.delete(serializedKey);
        } catch (final SerializationException exception) {
            LOGGER.error("Serialization exception occurred during findByKey operation. {}", exception.getMessage());
            throw exception;
        } catch (final RocksDBException exception) {
            LOGGER.error("RocksDBException occurred during deleteByKey operation. {}", exception.getMessage());
            throw new DeleteFailedException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deleteAll() throws DeleteAllFailedException {
        final RocksIterator iterator = rocksDB.newIterator();

        iterator.seekToFirst();
        final byte[] firstKey = getKey(iterator);

        iterator.seekToLast();
        final byte[] lastKey = getKey(iterator);

        if (firstKey == null || lastKey == null) {
            return;
        }

        try {
            rocksDB.deleteRange(firstKey, lastKey);
            rocksDB.delete(lastKey);
        } catch (final RocksDBException exception) {
            LOGGER.error("RocksDBException occurred during deleteAll operation. {}", exception.getMessage());
            throw new DeleteAllFailedException(exception.getMessage(), exception);
        } finally {
            iterator.close();
        }
    }

    private byte[] getKey(final RocksIterator iterator) {
        if (!iterator.isValid()) {
            return null;
        }
        return iterator.key();
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
