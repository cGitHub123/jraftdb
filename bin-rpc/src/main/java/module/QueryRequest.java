package module;

import java.io.Serializable;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public class QueryRequest implements Serializable {

    private static final long serialVersionUID = 9218253805003988802L;

    private boolean           readOnlySafe     = true;

    public boolean isReadOnlySafe() {
        return readOnlySafe;
    }

    public void setReadOnlySafe(boolean readOnlySafe) {
        this.readOnlySafe = readOnlySafe;
    }
}
