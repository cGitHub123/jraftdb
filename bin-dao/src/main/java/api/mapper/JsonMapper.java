
package api.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import api.mapper.exception.DeserializationException;
import api.mapper.exception.SerializationException;

import java.io.IOException;

/**
 * author caibin
 * date 2021-07-15
 */
public final class JsonMapper<T> implements Mapper<T> {

    private final Class<T> type;
    private final ObjectMapper mapper;

    public JsonMapper(final Class<T> type) {
        this.type = type;
        this.mapper = new ObjectMapper();
    }

    @Override
    public byte[] serialize(final T t) throws SerializationException {
        try {
            return mapper.writeValueAsBytes(t);
        } catch (final JsonProcessingException exception) {
            throw new SerializationException(exception.getMessage(), exception);
        }
    }

    @Override
    public T deserialize(final byte[] bytes) throws DeserializationException {
        try {
            return mapper.readValue(bytes, type);
        } catch (final IOException exception) {
            throw new DeserializationException(exception.getMessage(), exception);
        }
    }
}