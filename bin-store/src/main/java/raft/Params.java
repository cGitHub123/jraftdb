package raft;

import java.io.Serializable;

/**
 * @Author: caibin
 * @Date: 2021/7/23
 */
public class Params implements Serializable {

    private static final long serialVersionUID = 9218253805002988801L;

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
