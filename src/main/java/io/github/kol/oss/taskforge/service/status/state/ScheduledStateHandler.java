package io.github.kol.oss.taskforge.service.status.state;

import io.github.kol.oss.taskforge.core.cancel.CancelException;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.scheduler.IScheduler;
import io.github.kol.oss.taskforge.core.status.IEvent;
import io.github.kol.oss.taskforge.core.status.state.IStateHandler;
import io.github.kol.oss.taskforge.core.status.state.TaskState;

public class ScheduledStateHandler extends BasicStateHandler {
    protected volatile IStateHandler runningHandler;
    protected volatile IStateHandler cancelHandler;

    public ScheduledStateHandler(IStateHandler runningHandler, IStateHandler cancelHandler) {
        super(TaskState.SCHEDULED);

        this.runningHandler = runningHandler;
        this.cancelHandler = cancelHandler;
    }

    @Override
    public <T> void handle(IDescriptors<T> descriptors) {
        IEvent scheduledEvent = descriptors.getStatus().getEvent(TaskState.SCHEDULED);
        if (scheduledEvent.hasAlerted()) {
            throw new IllegalStateException("Task for action " + descriptors.getAction() + " was already scheduled");
        }

        super.handle(descriptors);

        boolean cancelCalled = this.checkCancelToken(descriptors);
        if(!cancelCalled) {
            this.schedule(descriptors);
        }
    }

    protected <T> boolean checkCancelToken(IDescriptors<T> descriptors) {
        try {
            descriptors.getCancelToken().throwIfCancelled();
        } catch (CancelException exception) {
            descriptors.setException(exception);
            this.cancelHandler.handle(descriptors);
            return true;
        }

        return false;
    }

    protected <T> void schedule(IDescriptors<T> descriptors) {
        IScheduler scheduler = descriptors.getScheduler();

        scheduler.schedule(() -> this.runningHandler.handle(descriptors));
    }
}
