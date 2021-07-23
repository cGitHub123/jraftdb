package module;

import java.io.Serializable;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public class DelRequest implements Serializable {

    private static final long serialVersionUID = 9218253805003988801L;

    private boolean           readOnlySafe     = true;

    private String sql;

    public boolean isReadOnlySafe() {
        return readOnlySafe;
    }

    public void setReadOnlySafe(boolean readOnlySafe) {
        this.readOnlySafe = readOnlySafe;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
