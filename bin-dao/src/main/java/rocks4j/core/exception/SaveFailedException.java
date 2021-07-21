package rocks4j.core.exception;

/**
 * author caibin
 * date 2021-07-15
 */
public final class SaveFailedException extends RocksIOException {

    public SaveFailedException(final String message) {
        super(message);
    }

    public SaveFailedException(
            final String message,
            final Throwable throwable
    ) {
        super(message, throwable);
    }
}
