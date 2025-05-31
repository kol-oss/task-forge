package io.github.kol.oss.taskforge.status.state;

import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.status.IStatus;
import io.github.kol.oss.taskforge.core.status.state.IStateHandler;
import io.github.kol.oss.taskforge.core.status.state.TaskState;
import io.github.kol.oss.taskforge.service.status.state.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("Basic Handler Tests")
public class BasicStateHandlerTest {
    static Stream<Arguments> stateHandlerProvider() {
        IStateHandler completedHandler = new CompletedStateHandler();
        IStateHandler failedHandler = new FailedStateHandler();
        IStateHandler cancelledHandler = new CanceledStateHandler();
        IStateHandler runningHandler = new RunningStateHandler(cancelledHandler, failedHandler, cancelledHandler);
        IStateHandler scheduledHandler = new ScheduledStateHandler(runningHandler, cancelledHandler);

        return Stream.of(
                Arguments.of(completedHandler, TaskState.COMPLETED),
                Arguments.of(failedHandler, TaskState.FAILED),
                Arguments.of(cancelledHandler, TaskState.CANCELED),
                Arguments.of(runningHandler, TaskState.RUNNING),
                Arguments.of(scheduledHandler, TaskState.SCHEDULED)
        );
    }

    @Test
    @DisplayName("handle should set correct state when handling descriptors")
    void handle_shouldSetCorrectState_whenUsingBasicStateHandler() {
        // Arrange
        final TaskState state = TaskState.RUNNING;
        IStatus mockStatus = mock(IStatus.class);
        IDescriptors<?> mockDescriptors = mock(IDescriptors.class);
        when(mockDescriptors.getStatus()).thenReturn(mockStatus);

        // Act
        IStateHandler basicHandler = new BasicStateHandler(state);
        basicHandler.handle(mockDescriptors);

        // Assert
        verify(mockDescriptors, times(1)).getStatus();
        verify(mockStatus, times(1)).setState(state);
    }

    @ParameterizedTest(name = "Handler {1} should return correct state")
    @DisplayName("getState should return correct state")
    @MethodSource("stateHandlerProvider")
    void getState_shouldReturnCorrectState_forAllHandlerTypes(IStateHandler handler, TaskState expectedState) {
        assertEquals(expectedState, handler.getState());
    }
}