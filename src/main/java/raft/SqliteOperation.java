package raft;

import java.io.Serializable;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public class SqliteOperation implements Serializable {

    private static final long serialVersionUID = -6597003954824547294L;

    public static final byte  QUERY              = 0x01;

    public static final byte  EXECUTE        = 0x02;

    private byte              op;
    private long              delta;
    private String sql;

    public static SqliteOperation createGet() {
        return new SqliteOperation(QUERY);
    }

    public static SqliteOperation createIncrement(final long delta) {
        return new SqliteOperation(EXECUTE, delta);
    }

    public SqliteOperation(byte op) {
        this(op, 0);
    }

    public SqliteOperation(byte op, long delta) {
        this.op = op;
        this.delta = delta;
    }

    public byte getOp() {
        return op;
    }

    public long getDelta() {
        return delta;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
