package io.github.kol.oss.taskforge.service.status.state;

import io.github.kol.oss.taskforge.core.status.state.TaskState;

public class CancelledStateHandler extends BasicStateHandler {
    public CancelledStateHandler() {
        super(TaskState.CANCELED);
    }
}
