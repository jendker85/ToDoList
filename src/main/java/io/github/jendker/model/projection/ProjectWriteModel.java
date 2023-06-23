package io.github.jendker.model.projection;

import io.github.jendker.model.Project;
import io.github.jendker.model.ProjectStep;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.*;

public class ProjectWriteModel {
    @NotBlank(message = "Project's description must not be empty")
    private String description;
    @Valid
    private List<ProjectStep> steps = new ArrayList<>();

    public ProjectWriteModel() {
        steps.add(new ProjectStep());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProjectStep> getSteps() {
        return steps;
    }

    public void setSteps(List<ProjectStep> steps) {
        this.steps = steps;
    }

    public Project toProject(){
        var result = new Project();
        steps.sort(Comparator.comparing(ProjectStep::getDaysToDeadline).reversed());
        result.setDescription(description);
        steps.forEach(step -> step.setProject(result));
        result.setSteps(new ArrayList<>(steps));
        return result;
    }
}
