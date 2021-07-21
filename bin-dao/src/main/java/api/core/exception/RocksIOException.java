package api.core.exception;

/**
 * author caibin
 * date 2021-07-15
 */
public abstract class RocksIOException extends Exception {

    public RocksIOException(final String message) {
        super(message);
    }

    public RocksIOException(
            final String message,
            final Throwable throwable
    ) {
        super(message, throwable);
    }
}
