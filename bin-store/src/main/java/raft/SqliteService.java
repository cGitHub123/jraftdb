package raft;

import module.DelRequest;
import module.GetRequest;
import module.PutRequest;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public interface SqliteService {

    void del(final boolean readOnlySafe, final SqliteClosure closure, DelRequest request);

    void get(final boolean readOnlySafe, final SqliteClosure closure, GetRequest request);

    void put(final SqliteClosure closure, PutRequest request);
}