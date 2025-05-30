package io.github.kol.oss.taskforge.service.status.state.when;

import io.github.kol.oss.taskforge.core.ITask;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.status.state.IStateHandler;
import io.github.kol.oss.taskforge.service.status.state.ScheduledStateHandler;

import java.util.Collection;

public abstract class WhenScheduledStateHandler<K> extends ScheduledStateHandler {
    protected volatile Collection<ITask<K>> tasks;
    protected volatile boolean finished;

    public WhenScheduledStateHandler(IStateHandler runningExecutor, IStateHandler cancelExecutor, Collection<ITask<K>> tasks) {
        super(runningExecutor, cancelExecutor);

        this.tasks = tasks;
        this.finished = false;
    }

    protected abstract <T> void schedule(IDescriptors<T> descriptors);

    protected <T> void scheduleGeneral(IDescriptors<T> descriptors) {
        super.schedule(descriptors);
        this.finished = true;
    }
}
