package io.github.jendker;

import io.github.jendker.model.Task;
import io.github.jendker.model.TaskGroup;
import io.github.jendker.model.TaskGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Warmup implements ApplicationListener <ContextRefreshedEvent> {
    public static final Logger logger = LoggerFactory.getLogger(Warmup.class);

    private final TaskGroupRepository groupRepository;

    public Warmup(TaskGroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        logger.info("Application warmup after context refreshed");
        final String description = "Application Context Event";
        if(!groupRepository.existsByDescription(description)){
            logger.info("No required group found! Adding it!");
            var group = new TaskGroup();
            group.setDescription(description);
            group.setTasks(Set.of(
                    new Task("Context closed event",null, group),
                    new Task("Context refreshed event",null, group),
                    new Task("Context stopped event",null, group),
                    new Task("Context started event",null, group)
            ));
            groupRepository.save(group);

        }

    }
}
