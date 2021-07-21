import java.io.Serializable;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public class ExecuteRequest implements Serializable {

    private static final long serialVersionUID = -5623664785560971849L;

    private String sql;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
