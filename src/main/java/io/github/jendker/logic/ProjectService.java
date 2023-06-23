package io.github.jendker.logic;

import io.github.jendker.TaskConfigurationProperties;
import io.github.jendker.model.*;
import io.github.jendker.model.projection.GroupReadModel;
import io.github.jendker.model.projection.GroupTaskWriteModel;
import io.github.jendker.model.projection.GroupWriteModel;
import io.github.jendker.model.projection.ProjectWriteModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectService {
    private ProjectRepository projectRepository;
    private TaskGroupRepository taskGroupRepository;
    private TaskConfigurationProperties config;
    private TaskGroupService service;

    public ProjectService(ProjectRepository projectRepository, TaskGroupRepository taskGroupRepository, TaskConfigurationProperties config, TaskGroupService service) {
        this.projectRepository = projectRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.config = config;
        this.service = service;
    }

    public Project save(ProjectWriteModel source){
        Project result = projectRepository.save(source.toProject());
        return result;
    }
    public List<Project> readAll(){
        return projectRepository.findAll();
    }


    public GroupReadModel createGroup(Integer projectId, LocalDateTime deadline){
        if (!config.getTemplate().isAllowMultipleTasks() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId)){
            throw new IllegalStateException("Only one undone group from project is allowed  ");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));
        var taskGroup = new GroupWriteModel();
        taskGroup.setDescription(project.getDescription());
        taskGroup.setTasks(project.getSteps().stream()
                        .map(projectStep -> {
                            var task = new GroupTaskWriteModel();
                            task.setDescription(projectStep.getDescription());
                            task.setDeadline(deadline.plusDays(projectStep.getDaysToDeadline()));
                            return task;
                        }).collect(Collectors.toList()));
        return service.createGroup(taskGroup,project);

    }
}
