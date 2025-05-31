package io.github.kol.oss.taskforge;

import io.github.kol.oss.taskforge.core.ITask;
import io.github.kol.oss.taskforge.core.cancel.CancelException;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.status.IEvent;
import io.github.kol.oss.taskforge.core.status.IStateExecutor;
import io.github.kol.oss.taskforge.core.status.IStatus;
import io.github.kol.oss.taskforge.core.status.state.TaskState;
import io.github.kol.oss.taskforge.service.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CancelTest {
    @Test
    @DisplayName("cancel should cancel token when not already cancelled")
    void cancel_shouldCancelToken_whenNotCancelled() {
        // Arrange
        IDescriptors<String> mockDescriptors = mock(IDescriptors.class);
        ICancelToken mockToken = mock(ICancelToken.class);
        when(mockDescriptors.getCancelToken()).thenReturn(mockToken);
        when(mockToken.isCancelled()).thenReturn(false);

        ITask<String> task = new Task<>(mockDescriptors, mock(IStateExecutor.class));

        // Act
        task.cancel();

        // Assert
        verify(mockToken, times(1)).cancel();
    }

    @Test
    @DisplayName("cancel should throw when already cancelled")
    void cancel_shouldThrow_whenAlreadyCancelled() {
        // Arrange
        IDescriptors<String> mockDescriptors = mock(IDescriptors.class);
        ICancelToken mockToken = mock(ICancelToken.class);
        when(mockDescriptors.getCancelToken()).thenReturn(mockToken);
        when(mockToken.isCancelled()).thenReturn(true);

        ITask<String> task = new Task<>(mockDescriptors, mock(IStateExecutor.class));

        // Act & Assert
        assertThrows(IllegalStateException.class, task::cancel);
    }

    @Test
    @DisplayName("getResult should throw CancelException when cancelled")
    void getResult_shouldThrowCancelException_whenCancelled() throws Exception {
        // Arrange
        IDescriptors<String> mockDescriptors = mock(IDescriptors.class);
        IStatus mockStatus = mock(IStatus.class);
        IEvent mockEvent = mock(IEvent.class);
        CancelException expectedException = new CancelException();

        when(mockDescriptors.getStatus()).thenReturn(mockStatus);
        when(mockStatus.getFinishedEvent()).thenReturn(mockEvent);
        when(mockStatus.getState()).thenReturn(TaskState.CANCELED);
        when(mockDescriptors.getException()).thenReturn(expectedException);

        Task<String> task = new Task<>(mockDescriptors, mock(IStateExecutor.class));

        // Act & Assert
        CancelException exception = assertThrows(CancelException.class, task::getResult);
        assertEquals(expectedException, exception);
    }
}
