package io.github.jendker.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table (name = "tasks")
public class Task extends BaseTask {

    private LocalDateTime deadline;
    @Embedded
    private Audit audit = new Audit();

    @ManyToOne
    @JoinColumn(name = "task_group_id")
    private TaskGroup group;

    public Task() {
    }
    public Task(String description, LocalDateTime deadline, TaskGroup group) {
        super(description);
        this.deadline = deadline;
        if (group != null){
            this.group = group;
        }
    }
    public Task(String description, LocalDateTime deadline) {
        this(description,deadline, null);
    }


    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    TaskGroup getGroup() {
        return group;
    }

    public void setGroup(TaskGroup group) {
        this.group = group;
    }


    public void updateFrom(Task source){
        super.updateFrom(source);
        deadline = source.deadline;
        group = source.group;
    }
}
