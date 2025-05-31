package io.github.kol.oss.taskforge.utils.builder;

import io.github.kol.oss.taskforge.core.action.IAction;
import io.github.kol.oss.taskforge.core.action.IEmptyVoidAction;
import io.github.kol.oss.taskforge.service.Task;
import io.github.kol.oss.taskforge.utils.TaskBuilder;
import io.github.kol.oss.taskforge.utils.TaskFactory;
import io.github.kol.oss.taskforge.utils.action.ActionFactory;

public class EmptyVoidTaskBuilder extends TaskBuilder<Void> {
    private final IEmptyVoidAction action;

    public EmptyVoidTaskBuilder(final IEmptyVoidAction action) {
        this.action = action;
    }

    @Override
    public Task<Void> construct() {
        IAction<Void> converted = ActionFactory.convert(action);
        return TaskFactory.create(converted, super.token, super.scheduler, super.status, super.executor);
    }
}