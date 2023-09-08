package io.github.jendker.reports;

import io.github.jendker.model.event.TaskDone;
import io.github.jendker.model.event.TaskEvent;
import io.github.jendker.model.event.TaskUndone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ChangedTaskEventListener {
    public static final Logger logger = LoggerFactory.getLogger(ChangedTaskEventListener.class);
    public final PersistedTaskEventRepository repository;

    public ChangedTaskEventListener(PersistedTaskEventRepository repository) {
        this.repository = repository;
    }

    @Async
    @EventListener
    public void on(TaskDone event){
        onChanged(event);
    }

    @Async
    @EventListener
    public void on(TaskUndone event){
        onChanged(event);;
    }

    private void onChanged(TaskEvent event) {
        logger.info("Got" + event);
        repository.save(new PersistedTaskEvent(event));
    }
}
