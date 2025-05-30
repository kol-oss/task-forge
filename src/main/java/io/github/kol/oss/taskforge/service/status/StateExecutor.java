package io.github.kol.oss.taskforge.service.status;

import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.status.IStateExecutor;
import io.github.kol.oss.taskforge.core.status.state.IStateHandler;
import io.github.kol.oss.taskforge.service.status.state.*;

public class StateExecutor implements IStateExecutor {
    protected volatile IStateHandler baseHandler;

    public StateExecutor(IStateHandler baseExecutor) {
        this.baseHandler = baseExecutor;
    }

    public StateExecutor() {
        IStateHandler completedExecutor = new CompletedStateHandler();
        IStateHandler failedExecutor = new FailedStateHandler();
        IStateHandler canceledExecutor = new CancelledStateHandler();

        this.baseHandler = new ScheduledStateHandler(
                new RunningStateHandler(
                        completedExecutor,
                        failedExecutor,
                        canceledExecutor
                ),
                canceledExecutor
        );
    }

    @Override
    public <T> void execute(IDescriptors<T> descriptors) {
        this.baseHandler.handle(descriptors);
    }
}
