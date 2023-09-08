package io.github.jendker.logic;

import io.github.jendker.model.Project;
import io.github.jendker.model.TaskGroup;
import io.github.jendker.model.TaskGroupRepository;
import io.github.jendker.model.TaskRepository;
import io.github.jendker.model.projection.GroupReadModel;
import io.github.jendker.model.projection.GroupWriteModel;

import java.util.List;
import java.util.stream.Collectors;


public class TaskGroupService {
    private TaskGroupRepository repository;
    private TaskRepository taskRepository;

    public TaskGroupService(TaskGroupRepository taskGroupRepository, TaskRepository taskRepository) {
        this.repository = taskGroupRepository;
        this.taskRepository = taskRepository;
    }

    public GroupReadModel createGroup(GroupWriteModel source, Project project){
        TaskGroup result = repository.save(source.toGroup(project));
        return new GroupReadModel(result);
    }
    public GroupReadModel createGroup(GroupWriteModel source){
        return createGroup(source,null);
    }

    public List<GroupReadModel> readAll(){
        return repository.findAll().stream()
                .map(GroupReadModel::new)
                .collect(Collectors.toList());
    }

    public void toggleGroup(int groupId){
        if (taskRepository.existsByDoneIsFalseAndGroup_Id(groupId)){
            throw new IllegalStateException("Group has undone tasks. Done all tasks first");
        }
        TaskGroup result =  repository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Task Group with given id not found"));
        result.toggle();
        repository.save(result);
    }
}
