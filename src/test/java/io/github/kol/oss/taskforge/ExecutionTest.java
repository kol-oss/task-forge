package io.github.kol.oss.taskforge;

import io.github.kol.oss.taskforge.core.ITask;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.status.IEvent;
import io.github.kol.oss.taskforge.core.status.IStateExecutor;
import io.github.kol.oss.taskforge.core.status.IStatus;
import io.github.kol.oss.taskforge.service.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ExecutionTest {
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
}
