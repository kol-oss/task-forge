package io.github.kol.oss.taskforge.service.status.state;

import io.github.kol.oss.taskforge.core.status.state.TaskState;

public class FailedStateHandler extends BasicStateHandler {
    public FailedStateHandler() {
        super(TaskState.FAILED);
    }
}
