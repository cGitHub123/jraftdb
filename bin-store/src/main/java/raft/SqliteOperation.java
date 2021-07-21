package raft;

import java.io.Serializable;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public class SqliteOperation implements Serializable {

    private static final long serialVersionUID = -6597003954824547294L;

    public static final byte  GET          = 0x01;

    public static final byte  SET        = 0x02;

    private byte              op;
    private String sql;

    public static SqliteOperation createGet() {
        return new SqliteOperation(GET);
    }

    public static SqliteOperation createIncrement() {
        return new SqliteOperation(SET);
    }

    public SqliteOperation(byte op) {
        this(op, 0);
    }

    public SqliteOperation(byte op, long delta) {
        this.op = op;
    }

    public byte getOp() {
        return op;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
