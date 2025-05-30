package io.github.kol.oss.taskforge.service.status.state.when;

import io.github.kol.oss.taskforge.core.ITask;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.status.IEvent;
import io.github.kol.oss.taskforge.core.status.state.IStateHandler;

import java.util.Collection;

public class WhenAllStateHandler<K> extends WhenScheduledStateHandler<K> {
    public WhenAllStateHandler(IStateHandler runningExecutor, IStateHandler cancelExecutor, Collection<ITask<K>> tasks) {
        super(runningExecutor, cancelExecutor, tasks);
    }

    @Override
    protected <T> void schedule(IDescriptors<T> descriptors) {
        if (this.tasks.isEmpty()) {
            super.scheduleGeneral(descriptors);
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
                        super.scheduleGeneral(descriptors);
                }
            });
        }
    }
}
