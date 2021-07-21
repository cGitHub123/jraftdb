package api.mapper.exception;

import java.io.IOException;

/**
 * author caibin
 * date 2021-07-15
 */
public abstract class SerDeException extends IOException {

    public SerDeException(final String message) {
        super(message);
    }

    public SerDeException(
            final String message,
            final Throwable throwable
    ) {
        super(message, throwable);
    }
}
