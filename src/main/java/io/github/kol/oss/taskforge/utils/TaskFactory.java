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
import io.github.kol.oss.taskforge.core.status.IStatus;
import io.github.kol.oss.taskforge.service.Task;
import io.github.kol.oss.taskforge.service.WhenAllTask;
import io.github.kol.oss.taskforge.service.WhenAnyTask;
import io.github.kol.oss.taskforge.service.action.when.WhenAllAction;
import io.github.kol.oss.taskforge.service.action.when.WhenAnyAction;
import io.github.kol.oss.taskforge.service.cancel.CancelToken;
import io.github.kol.oss.taskforge.service.descriptors.TaskDescriptors;
import io.github.kol.oss.taskforge.service.scheduler.UnboundedThreadScheduler;
import io.github.kol.oss.taskforge.service.status.StateExecutor;
import io.github.kol.oss.taskforge.service.status.TaskStatus;
import io.github.kol.oss.taskforge.utils.action.ActionFactory;

import java.util.Collection;

public class TaskFactory {
    public static final IScheduler DEFAULT_SCHEDULER = new UnboundedThreadScheduler();
    public static final IStateExecutor DEFAULT_EXECUTOR = new StateExecutor();

    public static ICancelToken getDefaultToken() {
        return new CancelToken();
    }

    public static IStatus getDefaultStatus() {
        return new TaskStatus();
    }

    public static <T> Task<T> create(IAction<T> action, ICancelToken token, IScheduler scheduler, IStatus status, IStateExecutor executor) {
        IDescriptors<T> descriptors = new TaskDescriptors<>(action, token, scheduler, status);
        return new Task<>(descriptors, executor);
    }

    public static <T> Task<T> create(IAction<T> action) {
        return create(action, getDefaultToken(), DEFAULT_SCHEDULER, getDefaultStatus(), DEFAULT_EXECUTOR);
    }

    public static <T> Task<T> create(IAction<T> action, IScheduler scheduler) {
        return create(action, getDefaultToken(), scheduler, getDefaultStatus(), DEFAULT_EXECUTOR);
    }

    public static <T, K> ITask<K> then(ITask<T> prev, IAction<K> action) {
        ITask<K> next = create(action);
        prev.then(next);

        return next;
    }

    public static <T> Task<T> create(IEmptyAction<T> action) {
        return create(action, DEFAULT_SCHEDULER);
    }

    public static <T> Task<T> create(IEmptyAction<T> action, IScheduler scheduler) {
        IAction<T> converted = ActionFactory.convert(action);
        return create(converted, getDefaultToken(), scheduler, getDefaultStatus(), DEFAULT_EXECUTOR);
    }

    public static Task<Void> create(IVoidAction action) {
        return create(action, DEFAULT_SCHEDULER);
    }

    public static Task<Void> create(IVoidAction action, IScheduler scheduler) {
        IAction<Void> converted = ActionFactory.convert(action);
        return create(converted, getDefaultToken(), scheduler, getDefaultStatus(), DEFAULT_EXECUTOR);
    }

    public static Task<Void> create(IEmptyVoidAction action) {
        return create(action, DEFAULT_SCHEDULER);
    }

    public static Task<Void> create(IEmptyVoidAction action, IScheduler scheduler) {
        IAction<Void> converted = ActionFactory.convert(action);
        return create(converted, getDefaultToken(), scheduler, getDefaultStatus(), DEFAULT_EXECUTOR);
    }

    public static <T> Task<Collection<T>> whenAll(Collection<ITask<T>> tasks) {
        IDescriptors<Collection<T>> descriptors = new TaskDescriptors<>(new WhenAllAction<>(tasks), getDefaultToken(), DEFAULT_SCHEDULER, getDefaultStatus());
        Task<Collection<T>> task = new WhenAllTask<>(descriptors, tasks);
        task.start();

        return task;
    }

    public static <T> Task<ITask<T>> whenAny(Collection<ITask<T>> tasks) {
        IDescriptors<ITask<T>> descriptors = new TaskDescriptors<>(new WhenAnyAction<>(tasks), getDefaultToken(), DEFAULT_SCHEDULER, getDefaultStatus());
        Task<ITask<T>> task = new WhenAnyTask<>(descriptors, tasks);
        task.start();

        return task;
    }
}
