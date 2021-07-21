package api.mapper.exception;

/**
 * author caibin
 * date 2021-07-15
 */
public final class SerializationException extends SerDeException {

    public SerializationException(final String message) {
        super(message);
    }

    public SerializationException(
            final String message,
            final Throwable throwable
    ) {
        super(message, throwable);
    }
}
