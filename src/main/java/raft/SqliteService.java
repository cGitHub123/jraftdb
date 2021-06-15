package raft;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public interface SqliteService {

    void get(final boolean readOnlySafe, final SqliteClosure closure);

    void incrementAndGet(final long delta, final SqliteClosure closure);
}