package rocks4j.mapper;

/**
 * author caibin
 * date 2021-07-15
 */
public interface MapperFactory {

    static <T> JsonMapper<T> createFor(final Class<T> type) {
        return new JsonMapper<>(type);
    }
}
