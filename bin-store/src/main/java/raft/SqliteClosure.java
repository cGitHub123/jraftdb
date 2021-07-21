package raft;

import com.alipay.sofa.jraft.Closure;
import raft.rpc.ValueResponse;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public abstract class SqliteClosure implements Closure {

    private ValueResponse valueResponse;
    private SqliteOperation sqliteOperation;

    public void setSqliteOperation(SqliteOperation sqliteOperation) {
        this.sqliteOperation = sqliteOperation;
    }

    public SqliteOperation getSqliteOperation() {
        return sqliteOperation;
    }

    public ValueResponse getValueResponse() {
        return valueResponse;
    }

    public void setValueResponse(ValueResponse valueResponse) {
        this.valueResponse = valueResponse;
    }

    protected void failure(final String errorMsg, final String redirect) {
        final ValueResponse response = new ValueResponse();
        response.setSuccess(false);
        response.setErrorMsg(errorMsg);
        response.setRedirect(redirect);
        setValueResponse(response);
    }

    protected void success(final long value) {
        final ValueResponse response = new ValueResponse();
        response.setValue(value);
        response.setSuccess(true);
        setValueResponse(response);
    }
}
