package io.github.kol.oss.taskforge.view;

import io.github.kol.oss.taskforge.core.ITask;
import io.github.kol.oss.taskforge.core.action.IAction;
import io.github.kol.oss.taskforge.core.action.IEmptyAction;
import io.github.kol.oss.taskforge.core.action.IEmptyVoidAction;
import io.github.kol.oss.taskforge.core.action.IVoidAction;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.scheduler.IScheduler;
import io.github.kol.oss.taskforge.core.status.IStateExecutor;
import io.github.kol.oss.taskforge.service.Task;
import io.github.kol.oss.taskforge.service.cancel.CancelToken;
import io.github.kol.oss.taskforge.service.descriptors.TaskDescriptors;
import io.github.kol.oss.taskforge.service.scheduler.UnboundedThreadScheduler;
import io.github.kol.oss.taskforge.service.status.StateExecutor;
import io.github.kol.oss.taskforge.service.status.TaskStatus;
import io.github.kol.oss.taskforge.view.action.ActionFactory;

public class TaskFactory {
    public static final ICancelToken DEFAULT_TOKEN = new CancelToken();
    public static final IScheduler DEFAULT_SCHEDULER = new UnboundedThreadScheduler();
    public static final IStateExecutor DEFAULT_EXECUTOR = new StateExecutor();

    public static <T> ITask<T> create(IAction<T> action, ICancelToken token, IScheduler scheduler, IStateExecutor executor) {
        IDescriptors<T> descriptors = new TaskDescriptors<>(action, token, scheduler, new TaskStatus(), null, null);
        return new Task<>(descriptors, executor);
    }

    public static <T> ITask<T> create(IAction<T> action, ICancelToken token, IScheduler scheduler) {
        return create(action, token, scheduler, DEFAULT_EXECUTOR);
    }

    public static <T> ITask<T> create(IAction<T> action, ICancelToken token) {
        return create(action, token, DEFAULT_SCHEDULER, DEFAULT_EXECUTOR);
    }

    public static <T> ITask<T> create(IAction<T> action) {
        return create(action, DEFAULT_TOKEN, DEFAULT_SCHEDULER, DEFAULT_EXECUTOR);
    }

    public static <T> ITask<T> create(IEmptyAction<T> action, ICancelToken token, IScheduler scheduler, IStateExecutor executor) {
        return create(ActionFactory.create(action), token, scheduler, executor);
    }

    public static <T> ITask<T> create(IEmptyAction<T> action, ICancelToken token, IScheduler scheduler) {
        return create(action, token, scheduler, DEFAULT_EXECUTOR);
    }

    public static <T> ITask<T> create(IEmptyAction<T> action, ICancelToken token) {
        return create(action, token, DEFAULT_SCHEDULER, DEFAULT_EXECUTOR);
    }

    public static <T> ITask<T> create(IEmptyAction<T> action) {
        return create(action, DEFAULT_TOKEN, DEFAULT_SCHEDULER, DEFAULT_EXECUTOR);
    }

    public static ITask<Void> create(IVoidAction action, ICancelToken token, IScheduler scheduler, IStateExecutor executor) {
        return create(ActionFactory.create(action), token, scheduler, executor);
    }

    public static ITask<Void> create(IVoidAction action, ICancelToken token, IScheduler scheduler) {
        return create(action, token, scheduler, DEFAULT_EXECUTOR);
    }

    public static ITask<Void> create(IVoidAction action, ICancelToken token) {
        return create(action, token, DEFAULT_SCHEDULER, DEFAULT_EXECUTOR);
    }

    public static ITask<Void> create(IVoidAction action) {
        return create(action, DEFAULT_TOKEN, DEFAULT_SCHEDULER, DEFAULT_EXECUTOR);
    }

    public static ITask<Void> create(IEmptyVoidAction action, ICancelToken token, IScheduler scheduler, IStateExecutor executor) {
        return create(ActionFactory.create(action), token, scheduler, executor);
    }

    public static ITask<Void> create(IEmptyVoidAction action, ICancelToken token, IScheduler scheduler) {
        return create(action, token, scheduler, DEFAULT_EXECUTOR);
    }

    public static ITask<Void> create(IEmptyVoidAction action, ICancelToken token) {
        return create(action, token, DEFAULT_SCHEDULER, DEFAULT_EXECUTOR);
    }

    public static ITask<Void> create(IEmptyVoidAction action) {
        return create(action, DEFAULT_TOKEN, DEFAULT_SCHEDULER, DEFAULT_EXECUTOR);
    }
}
