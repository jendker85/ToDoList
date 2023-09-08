package io.github.jendker.controller;

import io.github.jendker.model.BaseTask;
import io.github.jendker.model.Task;
import io.github.jendker.model.TaskRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;


@Controller
@RequestMapping("/tasks")
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final ApplicationEventPublisher eventPublisher;
    private final TaskRepository repository;

    public TaskController(ApplicationEventPublisher eventPublisher, TaskRepository repository) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
    }

    @GetMapping(params = {"!sort", "!page", "!size"})
    //@RequestMapping(method = RequestMethod.GET, value = "/tasks", params = {"!sort", "!page", "!size"})
    ResponseEntity<List<Task>> readAllTasks(){
        logger.warn("Exposing all tasks!");
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping
    //@RequestMapping(method = RequestMethod.GET, value = "/tasks")
    ResponseEntity<List<Task>> readAllTasks(Pageable page){
        logger.info("Pageable!");
        return ResponseEntity.ok(repository.findAll(page).getContent());
    }
    @GetMapping(value = "/{id}")
    //@RequestMapping(method = RequestMethod.GET, value ="/tasks/{id}")
    ResponseEntity<?> readTaskById(@PathVariable int id){

        return repository.findById(id)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());

    }

    @GetMapping("/search/done")
    ResponseEntity<List<Task>> readDoneTasks(@RequestParam(defaultValue = "true") boolean state){
        return ResponseEntity.ok(
                repository.findByDone(state)
        );
    }

    @GetMapping("/search/today")
    ResponseEntity<List<Task>> findTasksForToday(){
        return ResponseEntity.ok(
                repository.findAllByDoneIsFalseAndDeadlineIsNullOrDoneIsFalseAndDeadlineIsBefore(LocalDateTime.now())
        );
    }

    @PostMapping
    //@RequestMapping(method = RequestMethod.POST, value = "/tasks")
    ResponseEntity<Task> createTask(@RequestBody @Valid Task toCreate){
        Task newTask = repository.save(toCreate);
        return ResponseEntity.created(URI.create("/" + newTask.getId())).body(newTask);
    }

    @PutMapping("/{id}")
    //@RequestMapping(method = RequestMethod.PUT, value = "/tasks/{id}" )
    ResponseEntity<?> updateTask(@PathVariable int id, @RequestBody @Valid Task toUpdate){
        if(!repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        repository.findById(id)
                .ifPresent(task -> {
                    task.updateFrom(toUpdate);
                    repository.save(task);
                });

        return ResponseEntity.noContent().build();
    }
    @Transactional
    @PatchMapping(value = "/{id}" )
    public ResponseEntity<?> toggleTask(@PathVariable int id){
        if(!repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }

        repository.findById(id)
                .map(BaseTask::toggle)
                .ifPresent(eventPublisher::publishEvent);
        return ResponseEntity.noContent().build();
    }
}
