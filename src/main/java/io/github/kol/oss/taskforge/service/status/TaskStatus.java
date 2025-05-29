package io.github.kol.oss.taskforge.service.status;

import io.github.kol.oss.taskforge.core.status.IEvent;
import io.github.kol.oss.taskforge.core.status.IStatus;
import io.github.kol.oss.taskforge.core.status.state.TaskState;

import java.util.HashMap;
import java.util.Map;

public class TaskStatus implements IStatus {
    protected final Map<TaskState, IEvent> events = new HashMap<>();
    protected final IEvent finishedEvent = new StateEvent();

    protected TaskState state = TaskState.IDLE;

    public TaskStatus() {
        for (TaskState state : TaskState.values()) {
            this.events.put(state, new StateEvent());
        }
    }

    @Override
    public synchronized TaskState getState() {
        return this.state;
    }

    @Override
    public synchronized void setState(TaskState state) {
        IEvent event = this.events.get(state);

        this.state = state;
        event.alert();

        if (state == TaskState.COMPLETED || state == TaskState.FAILED || state == TaskState.CANCELED) {
            this.finishedEvent.alert();
        }
    }

    @Override
    public IEvent getEvent(TaskState state) {
        if (state == null) {
            throw new IllegalArgumentException("Can not obtain event for null task state");
        }

        return this.events.get(state);
    }

    @Override
    public IEvent getFinishedEvent() {
        return this.finishedEvent;
    }
}
