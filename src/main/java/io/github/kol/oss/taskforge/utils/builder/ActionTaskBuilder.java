package io.github.kol.oss.taskforge.utils.builder;

import io.github.kol.oss.taskforge.core.action.IAction;
import io.github.kol.oss.taskforge.service.Task;
import io.github.kol.oss.taskforge.utils.TaskBuilder;
import io.github.kol.oss.taskforge.utils.TaskFactory;

public class ActionTaskBuilder<T> extends TaskBuilder<T> {
    private final IAction<T> action;

    public ActionTaskBuilder(final IAction<T> action) {
        this.action = action;
    }

    @Override
    public Task<T> build() {
        return TaskFactory.create(this.action, super.token, super.scheduler, super.status, super.executor);
    }
}
