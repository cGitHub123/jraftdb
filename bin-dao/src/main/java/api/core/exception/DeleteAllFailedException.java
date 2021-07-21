package api.core.exception;

/**
 * author caibin
 * date 2021-07-15
 */
public final class DeleteAllFailedException extends RocksIOException {

    public DeleteAllFailedException(final String message) {
        super(message);
    }

    public DeleteAllFailedException(
            final String message,
            final Throwable throwable
    ) {
        super(message, throwable);
    }
}
