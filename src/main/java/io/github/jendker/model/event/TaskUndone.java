package io.github.jendker.model.event;

import io.github.jendker.model.BaseTask;
import io.github.jendker.model.Task;

import java.time.Clock;

public class TaskUndone extends TaskEvent {
    public TaskUndone(BaseTask source) {
        super(source.getId(), Clock.systemDefaultZone());
    }
}
