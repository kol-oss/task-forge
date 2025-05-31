package io.github.kol.oss.taskforge.utils.builder;

import io.github.kol.oss.taskforge.core.action.IAction;
import io.github.kol.oss.taskforge.core.action.IVoidAction;
import io.github.kol.oss.taskforge.service.Task;
import io.github.kol.oss.taskforge.utils.TaskBuilder;
import io.github.kol.oss.taskforge.utils.TaskFactory;
import io.github.kol.oss.taskforge.utils.action.ActionFactory;

public class VoidTaskBuilder extends TaskBuilder<Void> {
    private final IVoidAction action;

    public VoidTaskBuilder(final IVoidAction action) {
        this.action = action;
    }

    @Override
    public Task<Void> construct() {
        IAction<Void> converted = ActionFactory.convert(action);
        return TaskFactory.create(converted, super.token, super.scheduler, super.status, super.executor);
    }
}