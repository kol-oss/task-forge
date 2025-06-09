# TaskForge - Asynchronous Task Library for Java

## Overview

TaskForge is educational implementation of C# embedded Task Parallel Library on Java for easy creating and managing asynchronous tasks. The library supports task composition, cancellation, state observation, and flexible scheduling.

## Key Features

- **Multiple Action Types**: Support for various task signatures (with/without results, with/without cancellation)
- **Task Chaining**: `then` syntax for sequential task execution
- **Parallel Operations**: `WhenAll` and `WhenAny` for parallel task coordination
- **Cancellation Support**: Cooperative cancellation pattern
- **State Tracking**: Monitor task lifecycle through state listeners

## Task Cancellation

Tasks support cooperative cancellation through the `ICancelToken` interface. You can cancel a task in three ways:

1. **Explicit cancellation**:
```java
ITask<String> task = TaskFactory.create(...);
task.start();
// Later...
task.cancel();
```

2. **Using a pre-cancelled token**:

```java
import io.github.kol.oss.taskforge.utils.TaskBuilder;

ICancelToken token = new CancelToken();
token.cancel(); // Cancel before task starts

Task<String> task = TaskBuilder.createTask(...)
        .withToken(token)
        .build();
```

3. **Execution cancellation**:
```java
Task<Integer> task = TaskFactory.create((ICancelToken token) -> {
    for (int i = 0; i < 100; i++) {
        token.throwIfCancelled(); // Throws if cancelled
        // Continue work...
    }
    return result;
});
```

The execution cancellation system works cooperatively - tasks must periodically check the cancellation token to respond to cancellation requests. When cancelled, tasks transition to the `CANCELED` state and any waiting threads are notified.

## Custom Scheduling and Event Monitoring

TaskForge provides extensive customization capabilities through its scheduler and event monitoring interfaces. You can implement your own `IScheduler` to control exactly how tasks are executed in threading mechanisms. The library includes several built-in schedulers:

1. **`UnboundedThreadScheduler`**: Creates a new thread for each task (default)
2. **`BoundedThreadScheduler`**: Uses a fixed-size thread pool
3. **`CurrentThreadScheduler`**: Executes tasks immediately in the current thread

To use a custom scheduler:
```java
IScheduler myScheduler = new BoundedThreadScheduler(4); // 4-thread pool
Task<String> task = TaskFactory.create(() -> "Custom scheduled", myScheduler);
```

For event monitoring, implement `IStatus` and `IEvent` interfaces to track task state changes. The event system allows you to:
- Await specific state changes
- Add/remove listeners for state transitions
- Check if events have occurred

Example of custom event monitoring:
```java
// Finished event (task changed state into COMPLETED/CANCELLED/FAILED)
task.getFinishedEvent().addListener(() -> System.out.println("Task finished"));

// Event for selected state
task.getEvent(TaskState.FAILED).addListener(() -> System.out.println("Task failed"));
```

## Usage Examples

### Basic Task Creation with Different Action Types

```java
// 1. Action with execution cancellation and returnable result
Task<Integer> task1 = TaskFactory.create((ICancelToken token) -> {
    token.throwIfCancelled();
    return 42;
});

// 2. Action without execution cancellation and returnable result
Task<String> task2 = TaskFactory.create(() -> "Hello");

// 3. Action with execution cancellation and without result
Task<Void> task3 = TaskFactory.create((ICancelToken token) -> {
    token.throwIfCancelled();
    System.out.println("Working...");
});

// 4. Action without execution cancellation and without result
Task<Void> task4 = TaskFactory.create(() -> System.out.println("Simple action"));
```

### Task Chaining

```java
// Simple sequential chaining
Task<String> pipeline = TaskBuilder.createTask(() -> System.out.print("Start"))
    .withNext(System.out.print("-Middle"))
    .withNext(System.out.print("-End"))
    .build();

// Result: Start-Middle-End
pipeline.start();
pipeline.await();
```

### Parallel Task Execution

```java
// WhenAll - wait for all tasks to complete
List<Task<String>> tasks = Arrays.asList(
    TaskFactory.create(() -> {
        Thread.sleep(100);
        return "Task 1";
    }),
    TaskFactory.create(() -> {
        Thread.sleep(200);
        return "Task 2";
    }),
    TaskFactory.create(() -> "Task 3")
);

Task<Collection<String>> allTask = TaskFactory.whenAll(tasks);
allTask.start();
allTask.await();

// Output: ["Task 3", "Task 1", "Task 2"]
System.out.println(allTask.getResult());

// WhenAny - wait for first task to complete
Task<ITask<String>> anyTask = TaskFactory.whenAny(tasks);
anyTask.start();
anyTask.await();

// Output: "Task 3"
System.out.println(anyTask.getResult().getResult());
```

### Cancellation Examples

```java
// Cancellation during execution (user-specified)
ICancelToken token = new CancelToken();
Task<Integer> cancellableTask = TaskFactory.create((ICancelToken t) -> {
    for (int i = 0; i < 10; i++) {
        t.throwIfCancelled();
        Thread.sleep(100);
    }
    return 100;
}, token);

cancellableTask.start();
Thread.sleep(300);
cancellableTask.cancel();

// Pre-start cancellation
ICancelToken preCancelled = new CancelToken();
preCancelled.cancel();

Task<String> neverRuns = TaskFactory.create(() -> "This won't execute", preCancelled);
neverRuns.start();
neverRuns.await();

// Output: CANCELLED
System.out.println(neverRuns.getState());
```

### State Observation

```java
// Tracking task states
List<TaskState> observedStates = new ArrayList<>();

IStateListener listener = new IStateListener() {
    @Override
    public void onScheduled(TaskState state) {
        observedStates.add(state);
    }
    
    // ...
    
    @Override
    public void onFinished() {
        System.out.println("Task completed!");
    }
};

Task<String> observedTask = TaskBuilder.createTask(() -> "Observed")
    .withListener(listener)
    .build();

observedTask.start();
observedTask.await();

// Output: [SCHEDULED, RUNNING, COMPLETED]
System.out.println(observedStates);
```

### Advanced Task Composition

```java
// Combining sequential and parallel tasks
String result = "";
Task<String> combined = TaskBuilder.createTask(() -> result += "Start")
    .withNext(() -> result += "-Parallel:")
    .withNext(() -> {
        List<Task<String>> parallelTasks = Arrays.asList(
            TaskFactory.create(() -> "A"),
            TaskFactory.create(() -> "B"),
            TaskFactory.create(() -> "C")
        );
        
        Task<Collection<String>> parallel = TaskFactory.whenAll(parallelTasks);
        parallel.start();
        parallel.await();
        
        result += " " + String.join(",", parallel.getResult());
    })
    .build();

combined.start();
combined.await();

// "Output: Start-Parallel: A,B,C"
System.out.println(combined.getResult());
```

## Design Patterns Used

The library employs several OOP design patterns:

- **Factory Pattern**: `TaskFactory` simplifies task creation
- **Builder Pattern**: `TaskBuilder` enables fluent task composition
- **Strategy Pattern**: Custom `IScheduler` implementations
- **Observer Pattern**: `IStateListener` for state monitoring
- **Chain of Responsibility**: State transition handling
- **Command Pattern**: Action interfaces represent executable operations

## Core Components

### Action Types

1. **`IAction<T>`**: Operation with cancellation support that returns a value
   ```java
   Task<Integer> task = TaskFactory.create((ICancelToken token) -> {
       token.throwIfCancelled();
       return 42;
   });
   ```

2. **`IEmptyAction<T>`**: Operation without cancellation that returns a value
   ```java
   Task<String> task = TaskFactory.create(() -> "Hello");
   ```

3. **`IVoidAction`**: Operation with cancellation support that returns no value
   ```java
   Task<Void> task = TaskFactory.create((ICancelToken token) -> {
       token.throwIfCancelled();
       System.out.println("Working...");
   });
   ```

4. **`IEmptyVoidAction`**: Operation without cancellation that returns no value
   ```java
   Task<Void> task = TaskFactory.create(() -> System.out.println("Simple action"));
   ```

### Task Types

- **`Task<T>`**: Basic asynchronous operation
- **`WhenAllTask<T>`**: Composites multiple tasks
- **`WhenAnyTask<T>`**: Completes when any task finishes

### Supporting Components

- **`ICancelToken`**: Cooperative cancellation
- **`IScheduler`**: Controls task execution
- **`IStatus`**: Manages task state
- **`IStateExecutor`**: Handles state transitions