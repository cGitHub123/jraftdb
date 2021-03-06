package api.core.exception;

/**
 * author caibin
 * date 2021-07-15
 */
public final class DeleteFailedException extends RocksIOException {

    public DeleteFailedException(final String message) {
        super(message);
    }

    public DeleteFailedException(
            final String message,
            final Throwable throwable
    ) {
        super(message, throwable);
    }
}
