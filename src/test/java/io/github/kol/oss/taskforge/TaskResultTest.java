package io.github.kol.oss.taskforge;

import io.github.kol.oss.taskforge.core.ITask;
import io.github.kol.oss.taskforge.core.action.IAction;
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

class TaskResultTest {
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
    @DisplayName("then should chain tasks correctly")
    void then_shouldChainTasksCorrectly() {
        // Arrange
        IDescriptors<String> mockDescriptors = mock(IDescriptors.class);
        IStatus mockStatus = mock(IStatus.class);
        IEvent mockEvent = mock(IEvent.class);
        when(mockDescriptors.getStatus()).thenReturn(mockStatus);
        when(mockStatus.getFinishedEvent()).thenReturn(mockEvent);

        ITask<Integer> mockTask = mock(ITask.class);

        Task<String> task = new Task<>(mockDescriptors, mock(IStateExecutor.class));

        // Act
        ITask<Integer> result = task.then(mockTask);

        // Assert
        verify(mockEvent, times(1)).addListener(any(Runnable.class));
        assertEquals(mockTask, result);
    }
}