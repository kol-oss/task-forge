package io.github.kol.oss.taskforge.service.status;

import io.github.kol.oss.taskforge.core.ITask;
import io.github.kol.oss.taskforge.core.status.state.IStateHandler;
import io.github.kol.oss.taskforge.service.status.state.CanceledStateHandler;
import io.github.kol.oss.taskforge.service.status.state.CompletedStateHandler;
import io.github.kol.oss.taskforge.service.status.state.FailedStateHandler;
import io.github.kol.oss.taskforge.service.status.state.RunningStateHandler;
import io.github.kol.oss.taskforge.service.status.state.when.WhenAllStateHandler;

import java.util.Collection;

public class WhenAllStateExecutor<K> extends StateExecutor {
    public WhenAllStateExecutor(Collection<ITask<K>> tasks) {
        IStateHandler completedExecutor = new CompletedStateHandler();
        IStateHandler failedExecutor = new FailedStateHandler();
        IStateHandler canceledExecutor = new CanceledStateHandler();

        this.baseHandler = new WhenAllStateHandler<>(
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
