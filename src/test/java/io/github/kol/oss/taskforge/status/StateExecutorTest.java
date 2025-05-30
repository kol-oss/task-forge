package io.github.kol.oss.taskforge.status;

import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.status.state.IStateHandler;
import io.github.kol.oss.taskforge.service.status.StateExecutor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@DisplayName("State Executor Tests")
class StateExecutorTest {
    @Test
    @DisplayName("execute should call handle on base handler")
    @SuppressWarnings("unchecked")
    void execute_shouldCallHandleOnBaseHandler() {
        // Arrange
        IStateHandler mockHandler = mock(IStateHandler.class);
        StateExecutor executor = new StateExecutor(mockHandler);
        IDescriptors<String> mockDescriptors = mock(IDescriptors.class);

        // Act
        executor.execute(mockDescriptors);

        // Assert
        verify(mockHandler, times(1)).handle(mockDescriptors);
    }
}