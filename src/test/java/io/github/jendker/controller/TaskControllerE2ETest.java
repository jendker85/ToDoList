package io.github.jendker.controller;

import io.github.jendker.model.Task;
import io.github.jendker.model.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerE2ETest {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    TaskRepository repo;

    @Test
    void httpGet_returnsAllTasks(){
        //given
        var initial = repo.findAll().size();
        repo.save(new Task("foo", LocalDateTime.now()));
        repo.save(new Task("bar", LocalDateTime.now()));


        //when
        Task[] result = restTemplate.getForObject("http://localhost:" + port + "/tasks", Task[].class);

        //then
        assertThat(result).hasSize(initial + 2);

    }

    @Test
    void httpGet_returnsTaskById(){
        //given

        Task task = repo.save(new Task("foo", LocalDateTime.now()));
        var id = task.getId();

        //when
        Task result = restTemplate.getForObject("http://localhost:" + port + "/tasks/" + id, Task.class);


        //then
        assertThat(result.getDescription()).isEqualTo("foo");
    }

    @Test
    void httpPost_createTask() throws JSONException {
        //given
        var taskJsonObject = new JSONObject();
        taskJsonObject.put("description", "foo");
        taskJsonObject.put("done", true);
        taskJsonObject.put("deadline", LocalDateTime.now());
        taskJsonObject.put("group", null);
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request =
                new HttpEntity<String>(taskJsonObject.toString(),headers);

//        Task task = new Task("createdWithTest", LocalDateTime.now());
//        HttpEntity<Task> request1 = new HttpEntity<>(task);


        //when

        Task result = restTemplate.postForObject("http://localhost:" + port + "/tasks", request, Task.class);

        //then
        assertThat(result.getDescription()).isEqualTo("foo");
    }
}