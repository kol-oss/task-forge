package io.github.kol.oss.taskforge;

import io.github.kol.oss.taskforge.core.ITask;
import io.github.kol.oss.taskforge.core.action.IAction;
import io.github.kol.oss.taskforge.core.action.IEmptyAction;
import io.github.kol.oss.taskforge.core.action.IVoidAction;
import io.github.kol.oss.taskforge.core.cancel.CancelException;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.status.IStateExecutor;
import io.github.kol.oss.taskforge.core.status.IStateListener;
import io.github.kol.oss.taskforge.core.status.state.TaskState;
import io.github.kol.oss.taskforge.service.cancel.CancelToken;
import io.github.kol.oss.taskforge.service.status.StateExecutor;
import io.github.kol.oss.taskforge.service.status.state.CanceledStateHandler;
import io.github.kol.oss.taskforge.utils.TaskBuilder;
import io.github.kol.oss.taskforge.utils.TaskFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("End to End Task Tests")
public class EndToEndTaskTest {
    public static Stream<Arguments> getStateFlowInput() {
        IVoidAction completed = (token) -> {
        };
        IVoidAction failed = (token) -> {
            throw new IllegalArgumentException("Test-predefined fail");
        };
        IVoidAction canceled = (token) -> {
            throw new CancelException();
        };

        return Stream.of(
                Arguments.of(completed, List.of(TaskState.SCHEDULED, TaskState.RUNNING, TaskState.COMPLETED)),
                Arguments.of(failed, List.of(TaskState.SCHEDULED, TaskState.RUNNING, TaskState.FAILED)),
                Arguments.of(canceled, List.of(TaskState.SCHEDULED, TaskState.RUNNING, TaskState.CANCELED))
        );
    }

    public static IStateListener createStateListener(List<TaskState> states) {
        return new IStateListener() {
            @Override
            public void onScheduled() {
                states.add(TaskState.SCHEDULED);
            }

            @Override
            public void onRunning() {
                states.add(TaskState.RUNNING);
            }

            @Override
            public void onCanceled() {
                states.add(TaskState.CANCELED);
            }

            @Override
            public void onFailed() {
                states.add(TaskState.FAILED);
            }

            @Override
            public void onCompleted() {
                states.add(TaskState.COMPLETED);
            }

            @Override
            public void onFinished() {
            }
        };
    }

    @Test
    @Timeout(5)
    @DisplayName("cancel should cause throw in execution if token checked")
    public void shouldThrowCancelException_whenTokenCheckedDuringCancellation() throws InterruptedException {
        // Arrange
        final int timeout = 100;
        final int expectedIterations = 3;

        AtomicInteger actualIterations = new AtomicInteger(0);
        IAction<String> action = (token) -> {
            while (true) {
                token.throwIfCancelled();
                actualIterations.incrementAndGet();
                Thread.sleep(timeout);
            }
        };

        // Act
        ITask<String> task = TaskFactory.create(action);
        task.start();

        Thread.sleep(expectedIterations * timeout);
        task.cancel();
        task.await();

        // Assert
        assertEquals(TaskState.CANCELED, task.getState());
        assertEquals(expectedIterations, actualIterations.get());
        assertThrows(CancelException.class, task::getResult);
    }

    @Test
    @Timeout(1)
    @DisplayName("sequential tasks should execute in order")
    public void shouldExecuteTasksSequentially_whenChainedWithThen() throws InterruptedException {
        // Arrange
        final List<String> actual = new ArrayList<>();
        ITask<?> firstTask = TaskFactory.create(() -> actual.add("Hello"));
        ITask<?> secondTask = TaskFactory.create(() -> actual.add("My"));
        ITask<?> thirdTask = TaskFactory.create(() -> actual.add("World"));

        // Act
        firstTask
                .then(secondTask)
                .then(thirdTask);

        firstTask.start();
        thirdTask.await();

        // Assert: statuses
        assertEquals(TaskState.COMPLETED, firstTask.getState());
        assertEquals(TaskState.COMPLETED, secondTask.getState());
        assertEquals(TaskState.COMPLETED, thirdTask.getState());

        // Assert: actions
        List<String> expected = Arrays.asList("Hello", "My", "World");
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("whenAll should complete after all tasks finish")
    public void shouldCompleteAllTasks_whenUsingWhenAll() throws InterruptedException {
        // Arrange
        final Set<Integer> actual = new HashSet<>();
        final Collection<ITask<Boolean>> tasks = List.of(
                TaskFactory.create(() -> actual.add(1)),
                TaskFactory.create(() -> actual.add(2)),
                TaskFactory.create(() -> actual.add(3))
        );

        // Act
        ITask<Collection<Boolean>> whenAllTask = TaskFactory.whenAll(tasks);

        tasks.forEach(ITask::start);
        whenAllTask.await();

        // Assert
        final Set<Integer> expected = Set.of(1, 2, 3);

        assertEquals(TaskState.COMPLETED, whenAllTask.getState());
        assertNull(whenAllTask.getException());
        assertEquals(expected, actual);
    }

    @Test
    @Timeout(1)
    @DisplayName("whenAll should remain running until all tasks complete")
    public void shouldRemainRunning_whenNotAllTasksComplete() throws InterruptedException {
        // Arrange
        final Set<Integer> actual = new HashSet<>();

        ITask<Boolean> firstTask = TaskFactory.create(() -> actual.add(1));
        ITask<Boolean> secondTask = TaskFactory.create(() -> actual.add(2));
        final Collection<ITask<Boolean>> tasks = List.of(firstTask, secondTask);

        // Act: only second task is started
        ITask<Collection<Boolean>> whenAllTask = TaskFactory.whenAll(tasks);

        secondTask.start();
        Thread.sleep(100);

        // Assert: before all completed
        assertEquals(TaskState.RUNNING, whenAllTask.getState());
        assertNull(whenAllTask.getException());
        assertEquals(Set.of(2), actual);

        // Act: all tasks is started
        firstTask.start();
        whenAllTask.await();

        // Assert: after all completed
        assertEquals(TaskState.COMPLETED, whenAllTask.getState());
        assertNull(whenAllTask.getException());
        assertEquals(Set.of(1, 2), actual);
    }

    @Test
    @DisplayName("whenAny should complete with first finished task")
    public void shouldCompleteWithFirstTask_whenUsingWhenAny() throws Exception {
        // Arrange
        final Set<Integer> actual = new HashSet<>();

        ITask<Boolean> firstTask = TaskFactory.create(() -> actual.add(1));
        ITask<Boolean> secondTask = TaskFactory.create(() -> actual.add(2));
        final Collection<ITask<Boolean>> tasks = List.of(firstTask, secondTask);

        // Act: only second task is started
        ITask<ITask<Boolean>> whenAnyTask = TaskFactory.whenAny(tasks);

        secondTask.start();
        whenAnyTask.await();

        // Assert: only second task is completed
        assertEquals(TaskState.COMPLETED, whenAnyTask.getState());
        assertNull(whenAnyTask.getException());
        assertEquals(secondTask, whenAnyTask.getResult());
        assertEquals(Set.of(2), actual);

        // Act: all tasks are started
        firstTask.start();
        firstTask.await();

        // Assert: all tasks are completed
        assertEquals(TaskState.COMPLETED, whenAnyTask.getState());
        assertNull(whenAnyTask.getException());
        assertEquals(secondTask, whenAnyTask.getResult());
        assertEquals(Set.of(1, 2), actual);
    }

    @Test
    @Timeout(1)
    @DisplayName("canceled state handler should cancel task without exception")
    public void shouldCancelTaskWithoutException_whenUsingCanceledStateHandler() throws Exception {
        // Arrange
        IStateExecutor executor = new IStateExecutor() {
            @Override
            public <T> void execute(IDescriptors<T> descriptors) {
                new CanceledStateHandler().handle(descriptors);
            }
        };

        IEmptyAction<String> action = () -> "Action without cancellation";

        // Act
        ITask<String> task = TaskBuilder.createTask(action)
                .withExecutor(executor)
                .build();

        task.start();
        task.await();

        // Assert: cancel called but no exception
        assertEquals(TaskState.CANCELED, task.getState());
        assertThrows(NullPointerException.class, task::getResult);
        assertNull(task.getException());
    }

    @ParameterizedTest
    @MethodSource("getStateFlowInput")
    @Timeout(1)
    @DisplayName("task should follow expected state flow")
    public void shouldFollowExpectedStateFlow_whenTaskExecutes(IVoidAction action, List<TaskState> expected) throws Exception {
        // Arrange
        final List<TaskState> actual = new ArrayList<>();
        final IStateListener listener = createStateListener(actual);

        TaskBuilder<Void> builder = TaskBuilder
                .createTask(action)
                .withExecutor(new StateExecutor())
                .withListener(listener);

        // Act
        ITask<Void> task = builder.build();
        task.start();
        task.await();

        // Assert: default state flow
        assertEquals(expected, actual);
    }

    @Test
    @Timeout(1)
    @DisplayName("canceled token should prevent task from running")
    public void shouldCancelTaskImmediately_whenTokenPreCanceled() throws InterruptedException {
        // Arrange
        final List<TaskState> actual = new ArrayList<>();
        final IStateListener listener = createStateListener(actual);

        IAction<String> action = (token) -> "Hello, World!";
        ICancelToken token = new CancelToken();

        // Act
        ITask<String> task = TaskBuilder
                .createTask(action)
                .withToken(token)
                .withExecutor(new StateExecutor())
                .withListener(listener)
                .build();

        token.cancel();

        task.start();
        task.await();

        // Assert: default state flow
        assertEquals(List.of(TaskState.SCHEDULED, TaskState.CANCELED), actual);
    }
}
