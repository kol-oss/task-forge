package io.github.kol.oss.taskforge.status.state;

import io.github.kol.oss.taskforge.core.cancel.CancelException;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.scheduler.IScheduler;
import io.github.kol.oss.taskforge.core.status.IEvent;
import io.github.kol.oss.taskforge.core.status.IStatus;
import io.github.kol.oss.taskforge.core.status.state.IStateHandler;
import io.github.kol.oss.taskforge.core.status.state.TaskState;
import io.github.kol.oss.taskforge.service.status.state.ScheduledStateHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Scheduled State Handler Tests")
class ScheduledStateHandlerTest {

    @Test
    @DisplayName("handle should throw IllegalStateException when task already scheduled")
    void handle_shouldThrowException_whenTaskAlreadyScheduled() {
        // Arrange
        IStateHandler mockRunningHandler = mock(IStateHandler.class);
        IStateHandler mockCancelHandler = mock(IStateHandler.class);
        ScheduledStateHandler handler = new ScheduledStateHandler(mockRunningHandler, mockCancelHandler);

        IEvent mockEvent = mock(IEvent.class);
        when(mockEvent.hasAlerted()).thenReturn(true);

        IStatus mockStatus = mock(IStatus.class);
        when(mockStatus.getEvent(TaskState.SCHEDULED)).thenReturn(mockEvent);

        IDescriptors<?> mockDescriptors = mock(IDescriptors.class);
        when(mockDescriptors.getStatus()).thenReturn(mockStatus);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> handler.handle(mockDescriptors));
        verify(mockEvent, times(1)).hasAlerted();
    }

    @Test
    @DisplayName("handle should schedule task when not cancelled")
    void handle_shouldScheduleTask_whenNotCancelled() throws CancelException {
        // Arrange
        IStateHandler mockRunningHandler = mock(IStateHandler.class);
        IStateHandler mockCancelHandler = mock(IStateHandler.class);
        ScheduledStateHandler handler = new ScheduledStateHandler(mockRunningHandler, mockCancelHandler);

        IEvent mockEvent = mock(IEvent.class);
        when(mockEvent.hasAlerted()).thenReturn(false);

        IStatus mockStatus = mock(IStatus.class);
        when(mockStatus.getEvent(TaskState.SCHEDULED)).thenReturn(mockEvent);

        IScheduler mockScheduler = mock(IScheduler.class);
        ICancelToken mockCancelToken = mock(ICancelToken.class);

        IDescriptors<?> mockDescriptors = mock(IDescriptors.class);
        when(mockDescriptors.getStatus()).thenReturn(mockStatus);
        when(mockDescriptors.getScheduler()).thenReturn(mockScheduler);
        when(mockDescriptors.getCancelToken()).thenReturn(mockCancelToken);

        // Act
        handler.handle(mockDescriptors);

        // Assert
        verify(mockStatus, times(1)).setState(TaskState.SCHEDULED);
        verify(mockCancelToken, times(1)).throwIfCancelled();
        verify(mockScheduler, times(1)).schedule(any(Runnable.class));
        verify(mockCancelHandler, never()).handle(mockDescriptors);
    }

    @Test
    @DisplayName("handle should cancel task when cancellation requested")
    void handle_shouldCancelTask_whenCancellationRequested() throws CancelException {
        // Arrange
        IStateHandler mockRunningHandler = mock(IStateHandler.class);
        IStateHandler mockCancelHandler = mock(IStateHandler.class);
        ScheduledStateHandler handler = new ScheduledStateHandler(mockRunningHandler, mockCancelHandler);

        IEvent mockEvent = mock(IEvent.class);
        when(mockEvent.hasAlerted()).thenReturn(false);

        IStatus mockStatus = mock(IStatus.class);
        when(mockStatus.getEvent(TaskState.SCHEDULED)).thenReturn(mockEvent);

        IScheduler mockScheduler = mock(IScheduler.class);
        ICancelToken mockCancelToken = mock(ICancelToken.class);
        doThrow(new CancelException()).when(mockCancelToken).throwIfCancelled();

        IDescriptors<?> mockDescriptors = mock(IDescriptors.class);
        when(mockDescriptors.getStatus()).thenReturn(mockStatus);
        when(mockDescriptors.getScheduler()).thenReturn(mockScheduler);
        when(mockDescriptors.getCancelToken()).thenReturn(mockCancelToken);

        // Act
        handler.handle(mockDescriptors);

        // Assert
        verify(mockStatus, times(1)).setState(TaskState.SCHEDULED);
        verify(mockCancelToken, times(1)).throwIfCancelled();
        verify(mockScheduler, never()).schedule(any(Runnable.class));
        verify(mockCancelHandler, times(1)).handle(mockDescriptors);
        verify(mockDescriptors, times(1)).setException(any(CancelException.class));
    }
}