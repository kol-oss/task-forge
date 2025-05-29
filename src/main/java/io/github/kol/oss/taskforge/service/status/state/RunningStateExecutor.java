package io.github.kol.oss.taskforge.service.status.state;

import io.github.kol.oss.taskforge.core.cancel.CancelException;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.status.state.IStateExecutor;
import io.github.kol.oss.taskforge.core.status.state.TaskState;

public class RunningStateExecutor extends BasicStateExecutor {
    protected volatile IStateExecutor completedExecutor;
    protected volatile IStateExecutor failedExecutor;
    protected volatile IStateExecutor canceledExecutor;

    public RunningStateExecutor(IStateExecutor completedExecutor, IStateExecutor failedExecutor, IStateExecutor canceledExecutor) {
        super(TaskState.RUNNING);

        this.completedExecutor = completedExecutor;
        this.failedExecutor = failedExecutor;
        this.canceledExecutor = canceledExecutor;
    }

    @Override
    public <T> void execute(IDescriptors<T> descriptors) {
        super.execute(descriptors);

        ICancelToken token = descriptors.getCancelToken();

        try {
            token.throwIfCancelled();

            T value = descriptors.getAction().run(token);
            descriptors.setResult(value);
            this.completedExecutor.execute(descriptors);
        } catch (Exception exception) {
            descriptors.setException(exception);

            if (exception instanceof CancelException) {
                this.canceledExecutor.execute(descriptors);
            } else {
                this.failedExecutor.execute(descriptors);
            }
        }
    }
}
