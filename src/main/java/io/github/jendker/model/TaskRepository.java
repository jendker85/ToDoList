package io.github.jendker.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    List<Task> findAll();
    Page<Task> findAll(Pageable page);
    Optional<Task> findById(Integer id);
    List<Task> findByDone(@Param("state") boolean done);
    boolean existsById(Integer id);
    Task save (Task entity);
    boolean existsByDoneIsFalseAndGroup_Id(Integer groupId);

    List<Task> findAllByGroup_Id(Integer groupId);
    //List<Task> findAllByGroup_Id(Integer groupId);

    List<Task> findAllByDoneIsFalseAndDeadlineIsNullOrDoneIsFalseAndDeadlineIsBefore(LocalDateTime date);




}
