package io.github.jendker.logic;

import io.github.jendker.TaskConfigurationProperties;
import io.github.jendker.model.ProjectRepository;
import io.github.jendker.model.TaskGroupRepository;
import io.github.jendker.model.TaskRepository;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogicConfiguration {
    @Bean
    ProjectService projectService(ProjectRepository repository,
                           TaskGroupRepository taskGroupRepository,
                           TaskConfigurationProperties config,
                           TaskGroupService service){
        return new ProjectService(repository, taskGroupRepository, config, service);
    }

    @Bean
    TaskGroupService taskGroupService(TaskGroupRepository taskGroupRepository,
                                      TaskRepository taskRepository){
        return new TaskGroupService(taskGroupRepository, taskRepository);
    }

    @Bean
    TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}
