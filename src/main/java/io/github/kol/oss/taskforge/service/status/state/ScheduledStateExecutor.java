package io.github.kol.oss.taskforge.service.status.state;

import io.github.kol.oss.taskforge.core.cancel.CancelException;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.scheduler.IScheduler;
import io.github.kol.oss.taskforge.core.status.IEvent;
import io.github.kol.oss.taskforge.core.status.state.IStateExecutor;
import io.github.kol.oss.taskforge.core.status.state.TaskState;

public class ScheduledStateExecutor extends BasicStateExecutor {
    protected volatile IStateExecutor runningExecutor;
    protected volatile IStateExecutor cancelExecutor;

    public ScheduledStateExecutor(IStateExecutor runningExecutor, IStateExecutor cancelExecutor) {
        super(TaskState.SCHEDULED);

        this.runningExecutor = runningExecutor;
        this.cancelExecutor = cancelExecutor;
    }

    @Override
    public <T> void execute(IDescriptors<T> descriptors) {
        IEvent scheduledEvent = descriptors.getStatus().getEvent(TaskState.SCHEDULED);
        if (scheduledEvent.hasAlerted()) {
            throw new IllegalStateException("Task for action " + descriptors.getAction() + " was already scheduled");
        }

        super.execute(descriptors);

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
            this.cancelExecutor.execute(descriptors);
            return true;
        }

        return false;
    }

    protected <T> void schedule(IDescriptors<T> descriptors) {
        IScheduler scheduler = descriptors.getScheduler();

        scheduler.schedule(() -> this.runningExecutor.execute(descriptors));
    }
}
