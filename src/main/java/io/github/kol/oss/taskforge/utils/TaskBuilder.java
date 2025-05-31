package io.github.kol.oss.taskforge.utils;

import io.github.kol.oss.taskforge.core.action.IAction;
import io.github.kol.oss.taskforge.core.action.IEmptyAction;
import io.github.kol.oss.taskforge.core.action.IEmptyVoidAction;
import io.github.kol.oss.taskforge.core.action.IVoidAction;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;
import io.github.kol.oss.taskforge.core.scheduler.IScheduler;
import io.github.kol.oss.taskforge.core.status.IStateExecutor;
import io.github.kol.oss.taskforge.core.status.IStatus;
import io.github.kol.oss.taskforge.core.status.state.TaskState;
import io.github.kol.oss.taskforge.service.Task;
import io.github.kol.oss.taskforge.utils.builder.ActionTaskBuilder;
import io.github.kol.oss.taskforge.utils.builder.EmptyTaskBuilder;
import io.github.kol.oss.taskforge.utils.builder.EmptyVoidTaskBuilder;
import io.github.kol.oss.taskforge.utils.builder.VoidTaskBuilder;

public abstract class TaskBuilder<T> {
    protected ICancelToken token = TaskFactory.getDefaultToken();
    protected IStatus status = TaskFactory.getDefaultStatus();
    protected IScheduler scheduler = TaskFactory.getDefaultScheduler();
    protected IStateExecutor executor = TaskFactory.getDefaultExecutor();
    protected IStateListener listener;

    public static <T> TaskBuilder<T> createTask(IAction<T> action) {
        return new ActionTaskBuilder<>(action);
    }

    public static <T> TaskBuilder<T> createTask(IEmptyAction<T> action) {
        return new EmptyTaskBuilder<>(action);
    }

    public static TaskBuilder<Void> createTask(IVoidAction action) {
        return new VoidTaskBuilder(action);
    }

    public static TaskBuilder<Void> createTask(IEmptyVoidAction action) {
        return new EmptyVoidTaskBuilder(action);
    }

    public final TaskBuilder<T> withToken(final ICancelToken token) {
        if (token == null) {
            throw new IllegalArgumentException("Cancel token cannot be null");
        }

        this.token = token;
        return this;
    }

    public final TaskBuilder<T> withStatus(final IStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status handler cannot be null");
        }

        this.status = status;
        return this;
    }

    public final TaskBuilder<T> withScheduler(final IScheduler scheduler) {
        if (scheduler == null) {
            throw new IllegalArgumentException("Scheduler cannot be null");
        }

        this.scheduler = scheduler;
        return this;
    }

    public final TaskBuilder<T> withExecutor(final IStateExecutor executor) {
        if (executor == null) {
            throw new IllegalArgumentException("State executor cannot be null");
        }

        this.executor = executor;
        return this;
    }

    public final TaskBuilder<T> withListener(final IStateListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Task listener can not be null");
        }

        this.listener = listener;
        return this;
    }

    protected abstract Task<T> construct();

    public Task<T> build() {
        Task<T> task = this.construct();

        if (this.listener != null) {
            IStatus taskStatus = task.getStatus();
            for (TaskState state : TaskState.values()) {
                taskStatus.getEvent(state).addListener(listener.stateToFunction(state));
            }

            taskStatus.getFinishedEvent().addListener(() -> listener.onFinished());
        }

        return task;
    }
}
