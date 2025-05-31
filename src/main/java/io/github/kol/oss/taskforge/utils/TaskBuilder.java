package io.github.kol.oss.taskforge.utils;

import io.github.kol.oss.taskforge.core.ITask;
import io.github.kol.oss.taskforge.core.action.IAction;
import io.github.kol.oss.taskforge.core.action.IEmptyAction;
import io.github.kol.oss.taskforge.core.action.IEmptyVoidAction;
import io.github.kol.oss.taskforge.core.action.IVoidAction;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.scheduler.IScheduler;
import io.github.kol.oss.taskforge.core.status.IStateExecutor;
import io.github.kol.oss.taskforge.core.status.IStateListener;
import io.github.kol.oss.taskforge.core.status.IStatus;
import io.github.kol.oss.taskforge.core.status.state.TaskState;
import io.github.kol.oss.taskforge.service.Task;
import io.github.kol.oss.taskforge.service.descriptors.TaskDescriptors;
import io.github.kol.oss.taskforge.utils.action.ActionFactory;

import java.util.ArrayList;
import java.util.List;

public class TaskBuilder<T> {
    protected final IAction<T> action;
    protected final List<ITask<?>> nextTasks = new ArrayList<>();
    protected ICancelToken token = TaskFactory.getDefaultToken();
    protected IStatus status = TaskFactory.getDefaultStatus();
    protected IScheduler scheduler = TaskFactory.getDefaultScheduler();
    protected IStateExecutor executor = TaskFactory.getDefaultExecutor();
    protected IStateListener listener;

    private TaskBuilder(IAction<T> action) {
        this.action = action;
    }

    public static <T> TaskBuilder<T> createTask(IAction<T> action) {
        return new TaskBuilder<>(action);
    }

    public static <T> TaskBuilder<T> createTask(IEmptyAction<T> action) {
        IAction<T> converted = ActionFactory.convert(action);
        return new TaskBuilder<>(converted);
    }

    public static TaskBuilder<Void> createTask(IVoidAction action) {
        IAction<Void> converted = ActionFactory.convert(action);
        return new TaskBuilder<>(converted);
    }

    public static TaskBuilder<Void> createTask(IEmptyVoidAction action) {
        IAction<Void> converted = ActionFactory.convert(action);
        return new TaskBuilder<>(converted);
    }

    public TaskBuilder<T> withToken(final ICancelToken token) {
        if (token == null) {
            throw new IllegalArgumentException("Cancel token cannot be null");
        }

        this.token = token;
        return this;
    }

    public TaskBuilder<T> withStatus(final IStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status handler cannot be null");
        }

        this.status = status;
        return this;
    }

    public TaskBuilder<T> withScheduler(final IScheduler scheduler) {
        if (scheduler == null) {
            throw new IllegalArgumentException("Scheduler cannot be null");
        }

        this.scheduler = scheduler;
        return this;
    }

    public TaskBuilder<T> withExecutor(final IStateExecutor executor) {
        if (executor == null) {
            throw new IllegalArgumentException("State executor cannot be null");
        }

        this.executor = executor;
        return this;
    }

    public TaskBuilder<T> withListener(final IStateListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Task listener can not be null");
        }

        this.listener = listener;
        return this;
    }

    public <N> TaskBuilder<T> withNext(final IAction<N> action) {
        if (action == null) {
            throw new IllegalArgumentException("Next action cannot be null");
        }

        ITask<N> task = TaskFactory.create(action);
        this.nextTasks.add(task);
        return this;
    }

    public <N> TaskBuilder<T> withNext(final IEmptyAction<N> action) {
        IAction<N> converted = ActionFactory.convert(action);
        return this.withNext(converted);
    }

    public TaskBuilder<T> withNext(final IVoidAction action) {
        IAction<Void> converted = ActionFactory.convert(action);
        return this.withNext(converted);
    }

    public TaskBuilder<T> withNext(final IEmptyVoidAction action) {
        IAction<Void> converted = ActionFactory.convert(action);
        return this.withNext(converted);
    }

    public Task<T> build() {
        IDescriptors<T> descriptors = new TaskDescriptors<>(this.action, this.token, this.scheduler, this.status);
        Task<T> task = new Task<>(descriptors, this.executor);

        ITask<?> currTask = task;
        for (ITask<?> nextTask : this.nextTasks) {
            currTask.then(nextTask);
            currTask = nextTask;
        }

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
