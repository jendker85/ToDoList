package io.github.jendker.model;

import java.util.List;
import java.util.Optional;

public interface TaskGroupRepository {
    List<TaskGroup> findAll();
    Optional<TaskGroup> findById(Integer id);
    boolean existsById(Integer id);
    TaskGroup save (TaskGroup entity);
    boolean existsByDoneIsFalseAndProject_Id(Integer projectId);
}