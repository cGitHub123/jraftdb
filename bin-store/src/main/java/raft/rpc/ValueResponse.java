package raft.rpc;

import java.io.Serializable;

/**
 * author caibin@58.com
 * date 2021-06-15
 */
public class ValueResponse implements Serializable {

    private static final long serialVersionUID = -4220017686727146773L;

    private long              value;
    private boolean           success;

    private String            redirect;

    private String            errorMsg;

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getRedirect() {
        return this.redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getValue() {
        return this.value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public ValueResponse(long value, boolean success, String redirect, String errorMsg) {
        super();
        this.value = value;
        this.success = success;
        this.redirect = redirect;
        this.errorMsg = errorMsg;
    }

    public ValueResponse() {
        super();
    }

    @Override
    public String toString() {
        return "module.ValueResponse [value=" + this.value + ", success=" + this.success + ", redirect=" + this.redirect
               + ", errorMsg=" + this.errorMsg + "]";
    }

}
