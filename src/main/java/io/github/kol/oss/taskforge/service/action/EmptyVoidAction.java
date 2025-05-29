package io.github.kol.oss.taskforge.service.action;

import io.github.kol.oss.taskforge.core.action.IAction;
import io.github.kol.oss.taskforge.core.action.IEmptyVoidAction;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;

public class EmptyVoidAction implements IAction<Void> {
    protected volatile IEmptyVoidAction action;

    public EmptyVoidAction(IEmptyVoidAction action) {
        this.action = action;
    }

    @Override
    public Void run(ICancelToken token) throws Exception {
        this.action.run();
        return null;
    }

    public IEmptyVoidAction getAction() {
        return this.action;
    }
}
