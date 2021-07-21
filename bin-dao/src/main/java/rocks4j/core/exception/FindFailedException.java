package rocks4j.core.exception;

/**
 * author caibin
 * date 2021-07-15
 */
public final class FindFailedException extends RocksIOException {

    public FindFailedException(final String message) {
        super(message);
    }

    public FindFailedException(
            final String message,
            final Throwable throwable
    ) {
        super(message, throwable);
    }
}
