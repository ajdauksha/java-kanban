package manager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File testFile;
    private TaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        testFile = File.createTempFile("test", "tasks");
        manager = new FileBackedTaskManager(testFile);
    }


    @Test
    void shouldSaveAndLoadMultipleTasks() {
        Task task = new Task("Test Task", "Description", Status.NEW);
        manager.createTask(task);

        Epic epic = new Epic("Test Epic", "Epic Description");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Subtask Description", Status.IN_PROGRESS, epic.getId());
        manager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        List<Task> tasks = loadedManager.getAllTasks();
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(task, tasks.get(0), "Задачи не совпадают");

        List<Epic> epics = loadedManager.getAllEpics();
        assertEquals(1, epics.size(), "Неверное количество эпиков");
        assertEquals(epic, epics.get(0), "Эпики не совпадают");

        List<Subtask> subtasks = loadedManager.getAllSubtasks();
        assertEquals(1, subtasks.size(), "Неверное количество подзадач");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают");

        assertEquals(epic.getId(), subtasks.get(0).getEpicId(), "Неверная связь подзадачи с эпиком");
    }

    @Test
    void shouldSaveStateAfterTaskUpdate() {
        Task task = new Task("Test Task", "Description", Status.NEW);
        manager.createTask(task);

        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        Task loadedTask = loadedManager.getTaskById(task.getId());
        assertEquals(Status.IN_PROGRESS, loadedTask.getStatus(), "Статус задачи не обновился");
    }

    @Test
    void shouldSaveStateAfterTaskDeletion() {
        Task task = new Task("Test Task", "Description", Status.NEW);
        manager.createTask(task);

        manager.deleteTaskById(task.getId());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "Задача не была удалена");
    }

}
