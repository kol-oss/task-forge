package io.github.kol.oss.taskforge.service.status.state;

import io.github.kol.oss.taskforge.core.status.state.TaskState;

public class CancelledStateExecutor extends BasicStateExecutor {
    public CancelledStateExecutor() {
        super(TaskState.CANCELED);
    }
}
