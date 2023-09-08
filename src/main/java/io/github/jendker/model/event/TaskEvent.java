package io.github.jendker.model.event;

import io.github.jendker.model.BaseTask;
import io.github.jendker.model.Task;

import java.time.Clock;
import java.time.Instant;

public abstract class TaskEvent {

    public static TaskEvent changed (BaseTask source){
        return source.isDone() ? new TaskDone(source) : new TaskUndone(source);
    }
    private int taskId;
    private Instant occurrence;

    public TaskEvent(int taskId, Clock clock) {
        this.taskId = taskId;
        this.occurrence = Instant.now(clock);
    }

    public int getTaskId() {
        return taskId;
    }

    public Instant getOccurrence() {
        return occurrence;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "taskId=" + taskId +
                ", occurrence=" + occurrence +
                '}';
    }
}
