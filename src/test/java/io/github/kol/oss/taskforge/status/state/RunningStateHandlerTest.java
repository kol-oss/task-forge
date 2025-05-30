package io.github.kol.oss.taskforge.status.state;

import io.github.kol.oss.taskforge.core.action.IAction;
import io.github.kol.oss.taskforge.core.cancel.CancelException;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.status.IStatus;
import io.github.kol.oss.taskforge.core.status.state.IStateHandler;
import io.github.kol.oss.taskforge.core.status.state.TaskState;
import io.github.kol.oss.taskforge.service.status.state.RunningStateHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("Running State Handler Tests")
public class RunningStateHandlerTest {
    @Test
    @DisplayName("handle should complete task successfully when action succeeds")
    void handle_shouldCompleteTask_whenActionSucceeds() throws CancelException {
        // Arrange
        final int result = 2;
        IAction<Integer> action = (token) -> result;
        ICancelToken mockToken = mock(ICancelToken.class);
        IStatus mockStatus = mock(IStatus.class);

        @SuppressWarnings("unchecked")
        IDescriptors<Integer> mockDescriptors = (IDescriptors<Integer>) mock(IDescriptors.class);
        when(mockDescriptors.getCancelToken()).thenReturn(mockToken);
        when(mockDescriptors.getStatus()).thenReturn(mockStatus);
        when(mockDescriptors.getAction()).thenReturn(action);

        IStateHandler mockCompleted = mock(IStateHandler.class);
        IStateHandler mockFailed = mock(IStateHandler.class);
        IStateHandler mockCancelled = mock(IStateHandler.class);

        // Act
        IStateHandler runningHandler = new RunningStateHandler(mockCompleted, mockFailed, mockCancelled);
        runningHandler.handle(mockDescriptors);

        // Assert
        assertEquals(TaskState.RUNNING, runningHandler.getState());
        verify(mockDescriptors, times(1)).setResult(result);
        verify(mockDescriptors, times(0)).setException(any());
        verify(mockToken, times(1)).throwIfCancelled();

        verify(mockCompleted, times(1)).handle(mockDescriptors);
        verify(mockFailed, times(0)).handle(mockDescriptors);
        verify(mockCancelled, times(0)).handle(mockDescriptors);
    }

    @Test
    @DisplayName("handle should cancel task when cancellation requested")
    void handle_shouldCancelTask_whenCancellationRequested() throws CancelException {
        // Arrange
        final Exception exception = new CancelException();
        ICancelToken mockToken = mock(ICancelToken.class);
        doThrow(exception).when(mockToken).throwIfCancelled();
        IStatus mockStatus = mock(IStatus.class);

        @SuppressWarnings("unchecked")
        IDescriptors<Integer> mockDescriptors = mock(IDescriptors.class);
        when(mockDescriptors.getCancelToken()).thenReturn(mockToken);
        when(mockDescriptors.getStatus()).thenReturn(mockStatus);

        IStateHandler mockCompleted = mock(IStateHandler.class);
        IStateHandler mockFailed = mock(IStateHandler.class);
        IStateHandler mockCancelled = mock(IStateHandler.class);

        // Act
        IStateHandler runningHandler = new RunningStateHandler(mockCompleted, mockFailed, mockCancelled);
        runningHandler.handle(mockDescriptors);

        // Assert
        assertEquals(TaskState.RUNNING, runningHandler.getState());
        verify(mockDescriptors, times(0)).setResult(any());
        verify(mockDescriptors, times(1)).setException(exception);
        verify(mockToken, times(1)).throwIfCancelled();

        verify(mockCompleted, times(0)).handle(mockDescriptors);
        verify(mockFailed, times(0)).handle(mockDescriptors);
        verify(mockCancelled, times(1)).handle(mockDescriptors);
    }

    @Test
    @DisplayName("handle should fail task when action throws exception")
    void handle_shouldFailTask_whenActionThrowsException() throws Exception {
        // Arrange
        final Exception exception = new IllegalStateException("Custom exception");
        ICancelToken mockToken = mock(ICancelToken.class);
        @SuppressWarnings("unchecked")
        IAction<Void> mockAction = mock(IAction.class);
        doThrow(exception).when(mockAction).run(mockToken);
        IStatus mockStatus = mock(IStatus.class);

        @SuppressWarnings("unchecked")
        IDescriptors<Void> mockDescriptors = (IDescriptors<Void>) mock(IDescriptors.class);
        when(mockDescriptors.getCancelToken()).thenReturn(mockToken);
        when(mockDescriptors.getStatus()).thenReturn(mockStatus);
        when(mockDescriptors.getAction()).thenReturn(mockAction);

        IStateHandler mockCompleted = mock(IStateHandler.class);
        IStateHandler mockFailed = mock(IStateHandler.class);
        IStateHandler mockCancelled = mock(IStateHandler.class);

        // Act
        IStateHandler runningHandler = new RunningStateHandler(mockCompleted, mockFailed, mockCancelled);
        runningHandler.handle(mockDescriptors);

        // Assert
        assertEquals(TaskState.RUNNING, runningHandler.getState());
        verify(mockDescriptors, times(0)).setResult(any());
        verify(mockDescriptors, times(1)).setException(exception);
        verify(mockToken, times(1)).throwIfCancelled();

        verify(mockCompleted, times(0)).handle(mockDescriptors);
        verify(mockFailed, times(1)).handle(mockDescriptors);
        verify(mockCancelled, times(0)).handle(mockDescriptors);
    }
}