package me.nixuehan.tcc.resource;

import java.io.Serializable;

public class TccBundle implements Serializable {
    public Resource confirm;
    public Resource cancel;

    public TccBundle() {}

    public TccBundle(Resource confirm, Resource cancel) {
        this.confirm = confirm;
        this.cancel = cancel;
    }

    public Resource getConfirm() {
        return confirm;
    }

    public void setConfirm(Resource confirm) {
        this.confirm = confirm;
    }

    public Resource getCancel() {
        return cancel;
    }

    public void setCancel(Resource cancel) {
        this.cancel = cancel;
    }
}
