package api.mapper.exception;

/**
 * author caibin
 * date 2021-07-15
 */
public final class DeserializationException extends SerDeException {

    public DeserializationException(final String message) {
        super(message);
    }

    public DeserializationException(
            final String message,
            final Throwable throwable
    ) {
        super(message, throwable);
    }
}
