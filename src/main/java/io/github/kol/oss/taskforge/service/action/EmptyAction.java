package io.github.kol.oss.taskforge.service.action;

import io.github.kol.oss.taskforge.core.action.IAction;
import io.github.kol.oss.taskforge.core.action.IEmptyAction;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;

public class EmptyAction<T> implements IAction<T> {
    protected volatile IEmptyAction<T> action;

    public EmptyAction(IEmptyAction<T> action) {
        this.action = action;
    }

    @Override
    public T run(ICancelToken token) throws Exception {
        return this.action.run();
    }

    public IEmptyAction<T> getAction() {
        return this.action;
    }
}
