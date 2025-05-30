package io.github.kol.oss.taskforge.service.action.when;

import io.github.kol.oss.taskforge.core.ITask;
import io.github.kol.oss.taskforge.core.action.when.IWhenAnyTask;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;

import java.util.Collection;

public class WhenAnyAction<T> implements IWhenAnyTask<T> {
    protected volatile Collection<ITask<T>> tasks;

    public WhenAnyAction(Collection<ITask<T>> tasks) {
        this.tasks = tasks;
    }

    @Override
    public ITask<T> run(ICancelToken token) throws Exception {
        return this.tasks
                .stream()
                .filter((task) -> task.getStatus().getFinishedEvent().hasAlerted())
                .findFirst()
                .orElse(null);
    }
}
