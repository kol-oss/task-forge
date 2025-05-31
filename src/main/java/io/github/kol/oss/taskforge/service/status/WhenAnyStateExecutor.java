package io.github.kol.oss.taskforge.service.status;

import io.github.kol.oss.taskforge.core.ITask;
import io.github.kol.oss.taskforge.core.status.state.IStateHandler;
import io.github.kol.oss.taskforge.service.status.state.CanceledStateHandler;
import io.github.kol.oss.taskforge.service.status.state.CompletedStateHandler;
import io.github.kol.oss.taskforge.service.status.state.FailedStateHandler;
import io.github.kol.oss.taskforge.service.status.state.RunningStateHandler;
import io.github.kol.oss.taskforge.service.status.state.when.WhenAnyStateHandler;

import java.util.Collection;

public class WhenAnyStateExecutor<K> extends StateExecutor {
    public WhenAnyStateExecutor(Collection<ITask<K>> tasks) {
        IStateHandler completedExecutor = new CompletedStateHandler();
        IStateHandler failedExecutor = new FailedStateHandler();
        IStateHandler canceledExecutor = new CanceledStateHandler();

        this.baseHandler = new WhenAnyStateHandler<>(
                new RunningStateHandler(
                        completedExecutor,
                        failedExecutor,
                        canceledExecutor
                ),
                canceledExecutor,
                tasks
        );
    }
}