package io.github.kol.oss.taskforge.service.status.state;

import io.github.kol.oss.taskforge.core.status.state.TaskState;

public class FailedStateExecutor extends BasicStateExecutor {
    public FailedStateExecutor() {
        super(TaskState.FAILED);
    }
}
