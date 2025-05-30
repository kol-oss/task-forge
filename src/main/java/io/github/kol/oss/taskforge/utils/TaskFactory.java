package io.github.kol.oss.taskforge.utils;

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
import io.github.kol.oss.taskforge.service.cancel.CancelToken;
import io.github.kol.oss.taskforge.service.descriptors.TaskDescriptors;
import io.github.kol.oss.taskforge.service.scheduler.UnboundedThreadScheduler;
import io.github.kol.oss.taskforge.service.status.StateExecutor;
import io.github.kol.oss.taskforge.service.status.TaskStatus;
import io.github.kol.oss.taskforge.utils.action.ActionFactory;

public class TaskFactory {
    public static final ICancelToken DEFAULT_TOKEN = new CancelToken();
    public static final IScheduler DEFAULT_SCHEDULER = new UnboundedThreadScheduler();
    public static final IStateExecutor DEFAULT_EXECUTOR = new StateExecutor();
    public static final IStatus DEFAULT_STATUS = new TaskStatus();

    public static <T> Task<T> create(IAction<T> action, ICancelToken token, IScheduler scheduler, IStatus status, IStateExecutor executor) {
        IDescriptors<T> descriptors = new TaskDescriptors<>(action, token, scheduler, status);
        return new Task<>(descriptors, executor);
    }

    public static <T> Task<T> create(IAction<T> action) {
        return create(action, DEFAULT_TOKEN, DEFAULT_SCHEDULER, DEFAULT_STATUS, DEFAULT_EXECUTOR);
    }

    public static <T> Task<T> create(IEmptyAction<T> action) {
        IAction<T> converted = ActionFactory.convert(action);
        return create(converted, DEFAULT_TOKEN, DEFAULT_SCHEDULER, DEFAULT_STATUS, DEFAULT_EXECUTOR);
    }

    public static Task<Void> create(IVoidAction action) {
        IAction<Void> converted = ActionFactory.convert(action);
        return create(converted, DEFAULT_TOKEN, DEFAULT_SCHEDULER, DEFAULT_STATUS, DEFAULT_EXECUTOR);
    }

    public static Task<Void> create(IEmptyVoidAction action) {
        IAction<Void> converted = ActionFactory.convert(action);
        return create(converted, DEFAULT_TOKEN, DEFAULT_SCHEDULER, DEFAULT_STATUS, DEFAULT_EXECUTOR);
    }

    public static <T> Task<T> create(IAction<T> action, IScheduler scheduler) {
        return create(action, DEFAULT_TOKEN, scheduler, DEFAULT_STATUS, DEFAULT_EXECUTOR);
    }

    public static <T> Task<T> create(IEmptyAction<T> action, IScheduler scheduler) {
        IAction<T> converted = ActionFactory.convert(action);
        return create(converted, DEFAULT_TOKEN, scheduler, DEFAULT_STATUS, DEFAULT_EXECUTOR);
    }

    public static Task<Void> create(IVoidAction action, IScheduler scheduler) {
        IAction<Void> converted = ActionFactory.convert(action);
        return create(converted, DEFAULT_TOKEN, scheduler, DEFAULT_STATUS, DEFAULT_EXECUTOR);
    }

    public static Task<Void> create(IEmptyVoidAction action, IScheduler scheduler) {
        IAction<Void> converted = ActionFactory.convert(action);
        return create(converted, DEFAULT_TOKEN, scheduler, DEFAULT_STATUS, DEFAULT_EXECUTOR);
    }
}
