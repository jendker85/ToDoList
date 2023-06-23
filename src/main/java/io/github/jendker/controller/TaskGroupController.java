package io.github.jendker.controller;

import io.github.jendker.logic.TaskGroupService;
import io.github.jendker.model.*;
import io.github.jendker.model.projection.GroupReadModel;
import io.github.jendker.model.projection.GroupTaskWriteModel;
import io.github.jendker.model.projection.GroupWriteModel;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@IllegalExceptionProcessing
@Controller
@RequestMapping("/groups")
public class TaskGroupController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskGroupService service;
    private final TaskRepository taskRepository;


    public TaskGroupController(TaskGroupService service, TaskRepository taskRepository) {
        this.service = service;
        this.taskRepository = taskRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ResponseEntity<List<GroupReadModel>> readAllTaskGroups() {
        return ResponseEntity.ok(service.readAll());
    }



    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ResponseEntity<GroupReadModel> createGroup(@RequestBody @Valid GroupWriteModel toCreate) {
        GroupReadModel newGroup = service.createGroup(toCreate);
        return ResponseEntity.created(URI.create("/" + newGroup.getId())).body(newGroup);
    }


    @Transactional
    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> toggleGroup(@PathVariable int id) {
        service.toggleGroup(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ResponseEntity<List<Task>> readAllTasksFromGroup(@PathVariable int id) {
        return ResponseEntity.ok(taskRepository.findAllByGroup_Id(id));
    }




    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    String showTaskGroups(Model model) {
        model.addAttribute("group", new GroupWriteModel());
        return "groups";
    }

    @PostMapping(produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String addGroup(
            @ModelAttribute("group") @Valid GroupWriteModel current,
            BindingResult bindingResult,
            Model model) {

        if(bindingResult.hasErrors()){
            return "groups";
        }
        service.createGroup(current);
        model.addAttribute("group", new GroupWriteModel());
        model.addAttribute("groups", getGroups());
        model.addAttribute("message", "Dodano grupę!");
        return "groups";
    }
    @PostMapping(params = "editGroup", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String editGroup(
            @ModelAttribute("group") @Valid GroupWriteModel current,
            @RequestParam (value = "editGroup") GroupReadModel groupToEdit,
            BindingResult bindingResult,
            Model model) {

        if(bindingResult.hasErrors()){
            return "groups";
        }
        service.createGroup(current);
        model.addAttribute("group", groupToEdit);
        model.addAttribute("groups", getGroups());
        model.addAttribute("message", "Dodano grupę!");
        return "groups";
    }

    @PostMapping(params = "addTask",produces = MediaType.TEXT_HTML_VALUE)
    String addTask(@ModelAttribute("group") GroupWriteModel current){
        current.getTasks().add(new GroupTaskWriteModel());
        return "groups";
    }
    @PostMapping(params = "removeTask", produces = MediaType.TEXT_HTML_VALUE)
    String removeTask(@ModelAttribute("group") GroupWriteModel current, @RequestParam (value = "removeTask") int id){
        current.getTasks().remove(id);
        return "groups";
    }

    @PostMapping(params = "toggleGroup", produces = MediaType.TEXT_HTML_VALUE)
    String toggleGroup(@ModelAttribute("group") GroupWriteModel current,
                       Model model,
                       @RequestParam (value = "toggleGroup") int id){
        logger.warn("!!!!!!!" + id);
        try{
            service.toggleGroup(id);
            logger.warn("!!!!!!!!!" + id);
            model.addAttribute("message", "Grupa potwierdzona");
        }catch (IllegalStateException | IllegalArgumentException | NullPointerException e){
            model.addAttribute("message", "Błąd podczas potwierdzania grupy");
        }
        return "groups";
    }



    @ModelAttribute("groups")
    List<GroupReadModel> getGroups(){
        return service.readAll();
    }
}
