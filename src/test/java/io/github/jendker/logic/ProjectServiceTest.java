package io.github.jendker.logic;

import io.github.jendker.TaskConfigurationProperties;
import io.github.jendker.model.*;
import io.github.jendker.model.projection.GroupReadModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    @Test
    @DisplayName("should throw IllegalStateException when configured to allow just 1 group and the other undone group exists")
    void createGroup_noMultipleGroupsConfig_And_undoneGroupExists_throwsIllegalStateException() {
        //given
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(true);
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(false);
        //system under test
        var toTest = new ProjectService(null, mockGroupRepository,mockConfig, null);

        //when
        var exception = catchThrowable(()->toTest.createGroup(0,LocalDateTime.now()));

//            assertThatThrownBy(() ->toTest.createGroup(0,LocalDateTime.now()))
//                    .isInstanceOf(IllegalStateException.class);
//
//            assertThatExceptionOfType(IllegalStateException.class)
//                    .isThrownBy(()->toTest.createGroup(0,LocalDateTime.now()));
//
//             assertThatIllegalStateException()
//                   .isThrownBy(()->toTest.createGroup(0,LocalDateTime.now()));
            //that
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("one undone group");


    }



    @Test
    @DisplayName("should throw IllegalArgumentException when configured ok and no project for a given id")
    void createGroup_configurationOk_And_noProjects_throwsIllegalArgumentException() {
        //given
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        //and
        var mocRepository = mock(ProjectRepository.class);
        when(mocRepository.findById(anyInt())).thenReturn(Optional.empty());
        //system under test
        var toTest = new ProjectService(mocRepository, null,mockConfig, null);

        //when
        var exception = catchThrowable(()->toTest.createGroup(0,LocalDateTime.now()));

        //that
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when configured to allow just 1 group and no groups and no project for a given id")
    void createGroup_noMultipleGroupsConfig_And_noUndoneGroupExists_And_noProjects_throwsIllegalArgumentException() {
        //given
        TaskConfigurationProperties mockConfig = configurationReturning(false);
        //and
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(false);
        //and
        var mocRepository = mock(ProjectRepository.class);
        when(mocRepository.findById(anyInt())).thenReturn(Optional.empty());
        //system under test
        var toTest = new ProjectService(mocRepository, mockGroupRepository,mockConfig, null);

        //when
        var exception = catchThrowable(()->toTest.createGroup(0,LocalDateTime.now()));

        //that
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");
    }

    @Test
    @DisplayName("Should create a new group from the project")
    void createGroup_configurationOk_existingProject_createAndSaveGroup(){
        //given
        var today = LocalDate.now().atStartOfDay();
        //and
        var project = projectWith("bar",Set.of(-1,-2));
        var mocRepository = mock(ProjectRepository.class);
        when(mocRepository.findById(anyInt())).thenReturn(Optional.of(project));
        //and
        InMemoryGroupRepository inMemoryGroupRepo = inMemoryGroupRepository();
        int countBeforeCall = inMemoryGroupRepo.count();
        //and
        var serviceWithInMemRepo = dummyGroupService(inMemoryGroupRepo);
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        //system under test
        var toTest = new ProjectService(mocRepository, inMemoryGroupRepo,mockConfig, serviceWithInMemRepo);

        //when
        GroupReadModel result = toTest.createGroup(1,today);

        //that
        assertThat(result.getDescription()).isEqualTo("bar");
        assertThat(result.getDeadline()).isEqualTo(today.minusDays(1));
        assertThat(result.getTasks()).allMatch(task -> task.getDescription().equals("foo"));
        assertThat(countBeforeCall+1).isEqualTo(inMemoryGroupRepo.count());

    }

    private static TaskGroupService dummyGroupService(InMemoryGroupRepository inMemoryGroupRepo) {
        return new TaskGroupService(inMemoryGroupRepo, null);
    }

    private static TaskConfigurationProperties configurationReturning(boolean t) {
        var mockTemplate = mock(TaskConfigurationProperties.Template.class);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(t);
        //and
        var mockConfig = mock(TaskConfigurationProperties.class);
        when(mockConfig.getTemplate()).thenReturn(mockTemplate);
        return mockConfig;
    }

    private static TaskGroupRepository groupRepositoryReturning(boolean result) {
        var mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(result);
        return mockGroupRepository;
    }

    private Project projectWith(String projectDescription, Set<Integer> daysToDeadline){
        Set<ProjectStep> steps = daysToDeadline.stream()
                .map(days -> {
                    var step = mock(ProjectStep.class);
                    when(step.getDescription()).thenReturn("foo");
                    when(step.getDaysToDeadline()).thenReturn(Long.valueOf(days));
                    return step;
                }).collect(Collectors.toSet());
        var result = mock(Project.class);
        when(result.getDescription()).thenReturn(projectDescription);
        when(result.getSteps()).thenReturn((List<ProjectStep>) steps);
        return result;
    }

    private InMemoryGroupRepository inMemoryGroupRepository(){
        return new InMemoryGroupRepository();
    }
    private static class InMemoryGroupRepository implements TaskGroupRepository{

        private int index = 0;
        private Map<Integer, TaskGroup> map = new HashMap<>();

        public int count(){
            return map.values().size();
        }
        @Override
        public List<TaskGroup> findAll() {
            return new ArrayList<>(map.values());
        }

        @Override
        public Optional<TaskGroup> findById(Integer id) {
            return Optional.ofNullable(map.get(id));
        }

        @Override
        public boolean existsById(Integer id) {
            return false;
        }

        @Override
        public TaskGroup save(TaskGroup entity) {
            if(entity.getId() == 0){
                try{
                    var field = TaskGroup.class.getSuperclass().getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(entity,++index);
                }catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            map.put(++index,entity);
            return entity;
        }

        @Override
        public boolean existsByDoneIsFalseAndProject_Id(Integer projectId) {
            return map.values().stream()
                    .filter(group ->!group.isDone())
                    .anyMatch(group->group.getProject() !=null && group.getProject().getId() == projectId);
        }

    }
}