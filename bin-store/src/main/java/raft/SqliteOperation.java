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

    public static final byte  DEL        = 0x03;

    private byte              op;

    private Params KV;

    public static SqliteOperation createGet() {
        return new SqliteOperation(GET);
    }

    public static SqliteOperation createSet() {
        return new SqliteOperation(SET);
    }

    public static SqliteOperation createDel() {
        return new SqliteOperation(DEL);
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

    public Params getKV() {
        return KV;
    }

    public void setKV(Params KV) {
        this.KV = KV;
    }

    public class Params {

        private String k;

        private String v;

        public String getK() {
            return k;
        }

        public void setK(String k) {
            this.k = k;
        }

        public String getV() {
            return v;
        }

        public void setV(String v) {
            this.v = v;
        }
    }
}
