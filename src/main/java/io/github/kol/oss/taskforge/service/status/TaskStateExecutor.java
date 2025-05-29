package io.github.kol.oss.taskforge.service.status;

import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.status.ITaskStateExecutor;
import io.github.kol.oss.taskforge.core.status.state.IStateExecutor;
import io.github.kol.oss.taskforge.service.status.state.*;

public class TaskStateExecutor implements ITaskStateExecutor {
    protected volatile IStateExecutor baseExecutor;

    public TaskStateExecutor(IStateExecutor baseExecutor) {
        this.baseExecutor = baseExecutor;
    }

    public TaskStateExecutor() {
        IStateExecutor completedExecutor = new CompletedStateExecutor();
        IStateExecutor failedExecutor = new FailedStateExecutor();
        IStateExecutor canceledExecutor = new CancelledStateExecutor();

        this.baseExecutor = new ScheduledStateExecutor(
                new RunningStateExecutor(
                        completedExecutor,
                        failedExecutor,
                        canceledExecutor
                ),
                canceledExecutor
        );
    }

    @Override
    public <T> void execute(IDescriptors<T> descriptors) {
        this.baseExecutor.execute(descriptors);
    }
}
