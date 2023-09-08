package io.github.jendker.model;

import io.github.jendker.model.event.TaskEvent;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@MappedSuperclass
public class BaseTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    @NotBlank(message = "Task group's description must not be empty")
    public String  description;
    boolean done;

    public BaseTask() {
    }

    public BaseTask(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public TaskEvent toggle() {
        this.done = !this.done;
        return TaskEvent.changed(this);
    }
    public void updateFrom(BaseTask source){
        description = source.description;
        done = source.done;
    }



}
