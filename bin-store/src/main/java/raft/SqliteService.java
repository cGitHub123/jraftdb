package raft;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public interface SqliteService {

    void query(final boolean readOnlySafe, final SqliteClosure closure);

    void execute(final SqliteClosure closure);
}