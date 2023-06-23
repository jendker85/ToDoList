package io.github.jendker.model.projection;

import io.github.jendker.model.Task;
import io.github.jendker.model.TaskGroup;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class GroupReadModel {
    private int id;
    private String description;

    /*deadline from the latest task in group*/
    private LocalDateTime deadline;
    private boolean done;
    private List<GroupTaskReadModel> tasks = new ArrayList<>();

    public GroupReadModel() {
    }

    public GroupReadModel(TaskGroup source) {
        id = source.getId();
        done = source.isDone();
        description = source.getDescription();
        source.getTasks().stream()
                .map(Task::getDeadline)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .ifPresent(date -> deadline = date);
        tasks = source.getTasks().stream()
                .map(GroupTaskReadModel::new)
                .collect(Collectors.toList());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public List<GroupTaskReadModel> getTasks() {
        return tasks;
    }

    public void setTasks(List<GroupTaskReadModel> tasks) {
        this.tasks = tasks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
