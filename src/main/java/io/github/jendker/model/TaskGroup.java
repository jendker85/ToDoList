package io.github.jendker.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table (name = "task_groups")
public class TaskGroup extends BaseTask {
    @Embedded
    private Audit audit = new Audit();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
    private Set <Task> tasks;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;


    public TaskGroup() {
    }



    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void updateFrom(TaskGroup source) {
        super.updateFrom(source);
        tasks = source.tasks;
    }
}
