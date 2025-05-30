package io.github.kol.oss.taskforge.service.status.state.when;

import io.github.kol.oss.taskforge.core.ITask;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.status.IEvent;
import io.github.kol.oss.taskforge.core.status.state.IStateHandler;
import io.github.kol.oss.taskforge.service.status.state.ScheduledStateHandler;

import java.util.Collection;

public class WhenAllStateHandler<K> extends ScheduledStateHandler {
    protected volatile Collection<ITask<K>> tasks;
    protected volatile boolean finished;

    public WhenAllStateHandler(IStateHandler runningExecutor, IStateHandler cancelExecutor, Collection<ITask<K>> tasks) {
        super(runningExecutor, cancelExecutor);

        this.tasks = tasks;
        this.finished = false;
    }

    @Override
    protected <T> void schedule(IDescriptors<T> descriptors) {
        if (this.tasks.isEmpty()) {
            this.scheduleAll(descriptors);
            return;
        }

        for (ITask<K> task : this.tasks) {
            IEvent finishedEvent = task.getStatus().getFinishedEvent();
            finishedEvent.addListener(() -> {
                synchronized (descriptors) {
                    if (this.finished) return;

                    if (super.checkCancelToken(descriptors))
                        this.finished = true;
                    else
                        this.scheduleAll(descriptors);
                }
            });
        }
    }

    protected <T> void scheduleAll(IDescriptors<T> descriptors) {
        super.schedule(descriptors);
        this.finished = true;
    }
}
