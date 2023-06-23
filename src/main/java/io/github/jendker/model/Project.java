package io.github.jendker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    @NotBlank(message = "Project's description must not be empty")
    String description;
    @OneToMany(mappedBy = "project")
    private Set<TaskGroup> taskGroups;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private List<ProjectStep> steps;

    public Project() {
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    Set<TaskGroup> getTaskGroups() {
        return taskGroups;
    }

    void setTaskGroups(Set<TaskGroup> taskGroups) {
        this.taskGroups = taskGroups;
    }

    public List<ProjectStep> getSteps() {
        return steps;
    }

    public void setSteps(List<ProjectStep> projectSteps) {
        this.steps = projectSteps;
    }
}
