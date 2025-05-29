package io.github.kol.oss.taskforge.service.action;

import io.github.kol.oss.taskforge.core.action.IAction;
import io.github.kol.oss.taskforge.core.action.IVoidAction;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;

public class VoidAction implements IAction<Void> {
    protected volatile IVoidAction action;

    public VoidAction(IVoidAction action) {
        this.action = action;
    }

    @Override
    public Void run(ICancelToken token) throws Exception {
        this.action.run(token);
        return null;
    }

    public IVoidAction getAction() {
        return this.action;
    }
}