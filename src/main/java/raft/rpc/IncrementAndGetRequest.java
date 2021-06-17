package raft.rpc;

import java.io.Serializable;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public class IncrementAndGetRequest implements Serializable {

    private static final long serialVersionUID = -5623664785560971849L;

    private long              delta;

    private String sql;

    public long getDelta() {
        return this.delta;
    }

    public void setDelta(long delta) {
        this.delta = delta;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
