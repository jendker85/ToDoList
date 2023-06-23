package io.github.jendker.logic;

import io.github.jendker.model.TaskGroup;
import io.github.jendker.model.TaskGroupRepository;
import io.github.jendker.model.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskGroupServiceTest {

    @Test
    @DisplayName("should throw IllegalStateException when group has undone task")
    void toggleGroup_And_GroupHasUndoneTask_throwsIllegalStateException() {
        //given
        TaskRepository mockTaskRepository = taskRepositoryReturning(true);
        //system under test
        var toTest = new TaskGroupService(null, mockTaskRepository);

        //when
        var exception = catchThrowable(()->toTest.toggleGroup(0));

        //that
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("has undone tasks");

    }

    @Test
    @DisplayName("should throw IllegalArgumentException when group not exist")
    void toggleGroup_And_GroupNotExist_throwsIllegalArgumentException() {
        //given
        TaskRepository mockTaskRepository = taskRepositoryReturning(false);
        //and
        var mockTaskGroupRepository = mock(TaskGroupRepository.class);
        when(mockTaskGroupRepository.findById(anyInt())).thenReturn(Optional.empty());
        //system under test
        var toTest = new TaskGroupService(mockTaskGroupRepository, mockTaskRepository);

        //when
        var exception = catchThrowable(()->toTest.toggleGroup(0));

        //that
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");

    }

    @Test
    @DisplayName("should toggle task")
    void toggleGroup_And_GroupExist_And_AllTasksAreDone_toggleTheGroup() {
        //given
        TaskRepository mockTaskRepository = taskRepositoryReturning(false);
        //and
        var taskGroup = new TaskGroup();
        var statusBeforeCall = taskGroup.isDone();
        //and
        var mockTaskGroupRepository = mock(TaskGroupRepository.class);
        when(mockTaskGroupRepository.findById(anyInt())).thenReturn(Optional.of(taskGroup));
        //system under test
        var toTest = new TaskGroupService(mockTaskGroupRepository, mockTaskRepository);

        //when
        toTest.toggleGroup(0);


        //that
        assertThat(taskGroup.isDone()).isEqualTo(!statusBeforeCall);

    }




    private static TaskRepository taskRepositoryReturning(final boolean result) {
        var mockTaskRepository = mock(TaskRepository.class);
        when(mockTaskRepository.existsByDoneIsFalseAndGroup_Id(anyInt())).thenReturn(result);
        return mockTaskRepository;
    }
}