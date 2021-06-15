package raft;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public interface CounterService {

    /**
     * Get current value from counter
     *
     * Provide consistent reading if {@code readOnlySafe} is true.
     */
    void get(final boolean readOnlySafe, final CounterClosure closure);

    /**
     * Add delta to counter then get value
     */
    void incrementAndGet(final long delta, final CounterClosure closure);
}