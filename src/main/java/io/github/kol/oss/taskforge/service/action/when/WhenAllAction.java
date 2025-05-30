package io.github.kol.oss.taskforge.service.action.when;

import io.github.kol.oss.taskforge.core.ITask;
import io.github.kol.oss.taskforge.core.action.when.IWhenAllAction;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;
import io.github.kol.oss.taskforge.core.status.state.TaskState;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class WhenAllAction<T> implements IWhenAllAction<T> {
    protected volatile Collection<ITask<T>> tasks;

    public WhenAllAction(Collection<ITask<T>> tasks) {
        this.tasks = tasks;
    }

    @Override
    public Collection<T> run(ICancelToken token) throws Exception {
        if (this.anyAlerted(TaskState.FAILED)) {
            Collection<Exception> errors = this.collectErrors(TaskState.FAILED);
            throw this.combineErrors(errors);
        } else if (this.anyAlerted(TaskState.CANCELED)) {
            Collection<Exception> errors = this.collectErrors(TaskState.CANCELED);
            throw this.combineErrors(errors);
        }

        return this.collectResult(token);
    }

    protected boolean anyAlerted(TaskState state) {
        return this.tasks.stream()
                .anyMatch((task) -> task.getStatus()
                        .getEvent(state)
                        .hasAlerted()
                );
    }

    protected Collection<T> collectResult(ICancelToken token) throws Exception {
        Collection<T> results = new LinkedList<>();

        for (ITask<T> task : this.tasks)
            results.add(task.getResult());

        return results;
    }

    protected Collection<Exception> collectErrors(TaskState state) {
        return this.tasks
                .stream()
                .filter(task -> task.getStatus().getState().equals(state))
                .map(ITask::getException)
                .collect(Collectors.toList());
    }

    protected Exception combineErrors(Collection<Exception> errors) {
        Exception exception = null;

        for (Exception e : errors) {
            if (exception == null) {
                exception = e;
            } else {
                exception.addSuppressed(e);
            }
        }

        return exception;
    }
}
