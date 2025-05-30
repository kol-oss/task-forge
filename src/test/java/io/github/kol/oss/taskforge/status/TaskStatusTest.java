package io.github.kol.oss.taskforge.status;

import io.github.kol.oss.taskforge.core.status.IEvent;
import io.github.kol.oss.taskforge.core.status.state.TaskState;
import io.github.kol.oss.taskforge.service.status.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Task Status Tests")
class TaskStatusTest {
    @Test
    @DisplayName("Initial state should be IDLE")
    void initialState_shouldBeIdle() {
        // Arrange & Act
        TaskStatus status = new TaskStatus();

        // Assert
        assertEquals(TaskState.IDLE, status.getState());
    }

    @Test
    @DisplayName("setState should change current state")
    void setState_shouldChangeCurrentState() {
        // Arrange
        TaskStatus status = new TaskStatus();

        // Act
        status.setState(TaskState.RUNNING);

        // Assert
        assertEquals(TaskState.RUNNING, status.getState());
    }

    @Test
    @DisplayName("getEvent should return event for valid state")
    void getEvent_shouldReturnEvent_whenStateValid() {
        // Arrange
        TaskStatus status = new TaskStatus();

        // Act & Assert
        for (TaskState state : TaskState.values()) {
            assertNotNull(status.getEvent(state));
        }
    }

    @Test
    @DisplayName("getEvent should throw exception for null state")
    void getEvent_shouldThrowException_whenStateNull() {
        // Arrange
        TaskStatus status = new TaskStatus();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> status.getEvent(null));
    }

    @Test
    @DisplayName("setState should alert corresponding event")
    @Timeout(1)
    void setState_shouldAlertEvent_whenStateChanged() throws InterruptedException {
        // Arrange
        TaskStatus status = new TaskStatus();
        CountDownLatch latch = new CountDownLatch(1);
        IEvent event = status.getEvent(TaskState.RUNNING);

        new Thread(() -> {
            try {
                event.await();
                latch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        Thread.sleep(100);

        // Act
        status.setState(TaskState.RUNNING);

        // Assert
        assertTrue(latch.await(500, TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("setState should alert finishedEvent for terminal states")
    @Timeout(1)
    void setState_shouldAlertFinishedEvent_whenTerminalState() throws InterruptedException {
        // Arrange
        TaskStatus status = new TaskStatus();
        CountDownLatch latch = new CountDownLatch(1);
        IEvent finishedEvent = status.getFinishedEvent();

        new Thread(() -> {
            try {
                finishedEvent.await();
                latch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        Thread.sleep(100);

        // Act
        status.setState(TaskState.COMPLETED);

        // Assert
        assertTrue(latch.await(500, TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("setState should not alert finishedEvent for non-terminal states")
    @Timeout(1)
    void setState_shouldNotAlertFinishedEvent_whenNonTerminalState() throws InterruptedException {
        // Arrange
        TaskStatus status = new TaskStatus();
        CountDownLatch latch = new CountDownLatch(1);
        IEvent finishedEvent = status.getFinishedEvent();

        new Thread(() -> {
            try {
                finishedEvent.await();
                latch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        // Act
        status.setState(TaskState.RUNNING);

        // Assert
        assertFalse(latch.await(200, TimeUnit.MILLISECONDS));
        latch.countDown();
    }

    @Test
    @DisplayName("getFinishedEvent should return non-null event")
    void getFinishedEvent_shouldReturnNonNullEvent() {
        // Arrange
        TaskStatus status = new TaskStatus();

        // Act & Assert
        assertNotNull(status.getFinishedEvent());
    }
}