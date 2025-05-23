package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import utils.JsonHelper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTasksTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = JsonHelper.getCustomisedGson();
        client = HttpClient.newHttpClient();
        taskServer.start();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void testAddAndGetTask() throws Exception {
        Task task = new Task("Test", "Description", Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = sendPost("/tasks", taskJson);
        assertEquals(201, response.statusCode());

        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Test", tasks.get(0).getName());
    }

    @Test
    void testGetTaskById() throws Exception {
        Task task = manager.createTask(new Task("Test", "Descr", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now()));

        HttpResponse<String> response = sendGet("/tasks/" + task.getId());
        assertEquals(200, response.statusCode());

        Task receivedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(task.getId(), receivedTask.getId());
    }

    @Test
    void testDeleteTask() throws Exception {
        Task task = manager.createTask(new Task("Test", "Descr", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now()));

        HttpResponse<String> response = sendDelete("/tasks/" + task.getId());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void testHandleTimeOverlap() throws Exception {
        Task task1 = new Task("Task1", "Descr", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        manager.createTask(task1);

        Task task2 = new Task("Task2", "Descr", Status.NEW,
                Duration.ofMinutes(30), task1.getStartTime().plusMinutes(10));
        String json = gson.toJson(task2);

        HttpResponse<String> response = sendPost("/tasks", json);
        assertEquals(406, response.statusCode());
        assertEquals(1, manager.getAllTasks().size());
    }

    @Test
    void testUpdateTask() throws Exception {
        Task task = manager.createTask(new Task("Test", "Descr", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now()));
        task.setName("Updated");
        String json = gson.toJson(task);

        sendPost("/tasks", json);
        assertEquals("Updated", manager.getTaskById(task.getId()).get().getName());
    }

    @Test
    void testCreateEpic() throws Exception {
        Epic epic = new Epic("Test Epic", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        String json = gson.toJson(epic);

        HttpResponse<String> response = sendPost("/epics", json);

        assertEquals(201, response.statusCode());
        Epic createdEpic = gson.fromJson(response.body(), Epic.class);
        assertNotNull(createdEpic.getId());
        assertEquals(1, manager.getAllEpics().size());
    }

    @Test
    void testGetEpicById() throws Exception {
        Epic epic = manager.createEpic(new Epic("Test", "Desc", Duration.ofMinutes(30), LocalDateTime.now()));

        HttpResponse<String> response = sendGet("/epics/" + epic.getId());

        assertEquals(200, response.statusCode());
        Epic retrieved = gson.fromJson(response.body(), Epic.class);
        assertEquals(epic.getId(), retrieved.getId());
    }

    @Test
    void testGetAllEpics() throws Exception {
        manager.createEpic(new Epic("Epic1", "Desc1", Duration.ofMinutes(30), LocalDateTime.now()));
        manager.createEpic(new Epic("Epic2", "Desc2", Duration.ofMinutes(30), LocalDateTime.now().minusDays(1)));

        HttpResponse<String> response = sendGet("/epics");

        assertEquals(200, response.statusCode());
        List<Epic> epics = gson.fromJson(response.body(), new TypeToken<List<Epic>>(){}.getType());
        assertEquals(2, epics.size());
    }

    @Test
    void testDeleteEpic() throws Exception {
        Epic epic = manager.createEpic(new Epic("Test", "Desc", Duration.ofMinutes(30), LocalDateTime.now()));

        HttpResponse<String> response = sendDelete("/epics/" + epic.getId());

        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    void testCreateSubtaskWithEpic() throws Exception {
        Epic epic = manager.createEpic(new Epic("Parent", "", Duration.ofMinutes(30), LocalDateTime.now()));
        Subtask subtask = new Subtask("Sub", "Desc", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.now());

        HttpResponse<String> response = sendPost("/subtasks", gson.toJson(subtask));

        assertEquals(201, response.statusCode());
        Subtask created = gson.fromJson(response.body(), Subtask.class);
        assertEquals(epic.getId(), created.getEpicId());
        assertEquals(1, manager.getSubtasksByEpicId(epic.getId()).size());
    }

    @Test
    void testGetSubtaskById() throws Exception {
        Epic epic = manager.createEpic(new Epic("Parent", "", Duration.ofMinutes(30), LocalDateTime.now()));
        Subtask subtask = manager.createSubtask(
                new Subtask("Sub", "Desc", Status.NEW, epic.getId(), Duration.ofMinutes(15), LocalDateTime.now())
        );

        HttpResponse<String> response = sendGet("/subtasks/" + subtask.getId());

        assertEquals(200, response.statusCode());
        Subtask retrieved = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subtask.getId(), retrieved.getId());
    }

    @Test
    void testUpdateSubStatus() throws Exception {
        Epic epic = manager.createEpic(new Epic("Parent", "", Duration.ofMinutes(30), LocalDateTime.now()));
        Subtask subtask = manager.createSubtask(
                new Subtask("Sub", "Desc", Status.NEW, epic.getId(), Duration.ofMinutes(15), LocalDateTime.now().minusDays(1))
        );
        subtask.setStatus(Status.DONE);

        HttpResponse<String> response = sendPost("/subtasks", gson.toJson(subtask));

        assertEquals(201, response.statusCode());
        assertEquals(Status.DONE, manager.getSubtaskById(subtask.getId()).get().getStatus());
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).get().getStatus());
    }

    @Test
    void testDeleteSubtask() throws Exception {
        Epic epic = manager.createEpic(new Epic("Parent", "", Duration.ofMinutes(30), LocalDateTime.now()));
        Subtask subtask = manager.createSubtask(
                new Subtask("Sub", "Desc", Status.NEW, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now().minusDays(1))
        );

        HttpResponse<String> response = sendDelete("/subtasks/" + subtask.getId());

        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    void testGetHistory() throws Exception {
        Task task = manager.createTask(new Task("Test", "Descr", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now()));
        manager.getTaskById(task.getId());

        HttpResponse<String> response =  sendGet("/history");
        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());
        assertEquals(1, history.size());
        assertEquals(task.getId(), history.get(0).getId());
    }

    @Test
    void testGetPrioritizedTasks() throws Exception {
        Task task1 = new Task("Task1", "Descr", Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task("Task2", "Descr", Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusHours(1));
        manager.createTask(task1);
        manager.createTask(task2);

        HttpResponse<String> response = sendGet("/prioritized");
        List<Task> prioritized = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        assertEquals(2, prioritized.size());
        assertTrue(prioritized.get(0).getStartTime().isBefore(prioritized.get(1).getStartTime()));
    }

    @Test
    void testInvalidEndpoint() throws Exception {
        HttpResponse<String> response = sendGet("/invalid");
        assertEquals(404, response.statusCode());
    }


    private HttpResponse<String> sendGet(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080" + path))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPost(String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080" + path))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDelete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080" + path))
                .DELETE()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
