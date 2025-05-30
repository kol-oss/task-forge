package io.github.kol.oss.taskforge.utils.builder;

import io.github.kol.oss.taskforge.core.action.IAction;
import io.github.kol.oss.taskforge.core.action.IEmptyAction;
import io.github.kol.oss.taskforge.service.Task;
import io.github.kol.oss.taskforge.utils.TaskBuilder;
import io.github.kol.oss.taskforge.utils.TaskFactory;
import io.github.kol.oss.taskforge.utils.action.ActionFactory;

public class EmptyTaskBuilder<T> extends TaskBuilder<T> {
    private final IEmptyAction<T> action;

    public EmptyTaskBuilder(final IEmptyAction<T> action) {
        this.action = action;
    }

    @Override
    public Task<T> build() {
        IAction<T> converted = ActionFactory.convert(action);
        return TaskFactory.create(converted, super.token, super.scheduler, super.status, super.executor);
    }
}