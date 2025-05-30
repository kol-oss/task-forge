package io.github.kol.oss.taskforge;

import io.github.kol.oss.taskforge.core.ITask;
import io.github.kol.oss.taskforge.core.action.IAction;
import io.github.kol.oss.taskforge.core.cancel.CancelException;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.scheduler.IScheduler;
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

class TaskTest {
    @Test
    @DisplayName("start should execute task via executor")
    void start_shouldExecuteViaExecutor() {
        // Arrange
        IDescriptors<String> mockDescriptors = mock(IDescriptors.class);
        IStateExecutor mockExecutor = mock(IStateExecutor.class);
        ITask<String> task = new Task<>(mockDescriptors, mockExecutor);

        // Act
        task.start();

        // Assert
        verify(mockExecutor, times(1)).execute(mockDescriptors);
    }

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
    @DisplayName("getResult should return result when completed")
    void getResult_shouldReturnResult_whenCompleted() throws Exception {
        // Arrange
        final String expectedResult = "success result";

        IDescriptors<String> mockDescriptors = mock(IDescriptors.class);
        IStatus mockStatus = mock(IStatus.class);
        IEvent mockEvent = mock(IEvent.class);

        when(mockDescriptors.getStatus()).thenReturn(mockStatus);
        when(mockStatus.getFinishedEvent()).thenReturn(mockEvent);
        when(mockStatus.getState()).thenReturn(TaskState.COMPLETED);
        when(mockDescriptors.getResult()).thenReturn(expectedResult);

        ITask<String> task = new Task<>(mockDescriptors, mock(IStateExecutor.class));

        // Act
        String result = task.getResult();

        // Assert
        assertEquals(expectedResult, result);
        verify(mockEvent, times(1)).await();
    }

    @Test
    @DisplayName("getResult should throw when failed")
    void getResult_shouldThrow_whenFailed() throws Exception {
        // Arrange
        IDescriptors<String> mockDescriptors = mock(IDescriptors.class);
        IStatus mockStatus = mock(IStatus.class);
        IEvent mockEvent = mock(IEvent.class);
        final Exception expectedException = new Exception("Task failed");

        when(mockDescriptors.getStatus()).thenReturn(mockStatus);
        when(mockStatus.getFinishedEvent()).thenReturn(mockEvent);
        when(mockStatus.getState()).thenReturn(TaskState.FAILED);
        when(mockDescriptors.getException()).thenReturn(expectedException);

        ITask<String> task = new Task<>(mockDescriptors, mock(IStateExecutor.class));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, task::getResult);
        assertEquals(expectedException, exception);
    }

    @Test
    @DisplayName("then should chain tasks correctly")
    void then_shouldChainTasksCorrectly() {
        // Arrange
        IDescriptors<String> mockDescriptors1 = mock(IDescriptors.class);
        IStatus mockStatus1 = mock(IStatus.class);
        IEvent mockEvent1 = mock(IEvent.class);
        when(mockDescriptors1.getStatus()).thenReturn(mockStatus1);
        when(mockStatus1.getFinishedEvent()).thenReturn(mockEvent1);

        IDescriptors<Integer> mockDescriptors2 = mock(IDescriptors.class);
        ITask<Integer> mockTask2 = mock(ITask.class);

        Task<String> task1 = new Task<>(mockDescriptors1, mock(IStateExecutor.class));

        // Act
        ITask<Integer> result = task1.then(mockTask2);

        // Assert
        verify(mockEvent1, times(1)).addListener(any(Runnable.class));
        assertEquals(mockTask2, result);
    }

    @Test
    @DisplayName("getException should return exception from descriptors")
    void getException_shouldReturnExceptionFromDescriptors() {
        // Arrange
        IDescriptors<String> mockDescriptors = mock(IDescriptors.class);
        Exception expectedException = new Exception("test");
        when(mockDescriptors.getException()).thenReturn(expectedException);

        ITask<String> task = new Task<>(mockDescriptors, mock(IStateExecutor.class));

        // Act
        Exception result = task.getException();

        // Assert
        assertEquals(expectedException, result);
    }

    @Test
    @DisplayName("getAction should return action from descriptors")
    void getAction_shouldReturnActionFromDescriptors() {
        // Arrange
        IDescriptors<String> mockDescriptors = mock(IDescriptors.class);
        @SuppressWarnings("unchecked")
        IAction<String> mockAction = mock(IAction.class);
        when(mockDescriptors.getAction()).thenReturn(mockAction);

        Task<String> task = new Task<>(mockDescriptors, mock(IStateExecutor.class));

        // Act
        IAction<String> result = task.getAction();

        // Assert
        assertEquals(mockAction, result);
    }

    @Test
    @DisplayName("getScheduler should return scheduler from descriptors")
    void getScheduler_shouldReturnSchedulerFromDescriptors() {
        // Arrange
        IDescriptors<String> mockDescriptors = mock(IDescriptors.class);
        IScheduler mockScheduler = mock(IScheduler.class);
        when(mockDescriptors.getScheduler()).thenReturn(mockScheduler);

        Task<String> task = new Task<>(mockDescriptors, mock(IStateExecutor.class));

        // Act
        IScheduler result = task.getScheduler();

        // Assert
        assertEquals(mockScheduler, result);
    }

    @Test
    @DisplayName("getStatus should return status from descriptors")
    void getStatus_shouldReturnStatusFromDescriptors() {
        // Arrange
        IDescriptors<String> mockDescriptors = mock(IDescriptors.class);
        IStatus mockStatus = mock(IStatus.class);
        when(mockDescriptors.getStatus()).thenReturn(mockStatus);

        Task<String> task = new Task<>(mockDescriptors, mock(IStateExecutor.class));

        // Act
        IStatus result = task.getStatus();

        // Assert
        assertEquals(mockStatus, result);
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