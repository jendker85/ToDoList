package io.github.jendker.model.event;

import io.github.jendker.model.BaseTask;
import io.github.jendker.model.Task;

import java.time.Clock;

public class TaskDone extends TaskEvent {
    public TaskDone(BaseTask source) {
        super(source.getId(), Clock.systemDefaultZone());
    }
}
