package manager;

import exceptions.ManagerLoadException;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File testFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            testFile = Files.createTempFile("test", "tasks").toFile();
            return new FileBackedTaskManager(testFile);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать временный файл", e);
        }
    }

    @Test
    void shouldSaveAndLoadMultipleTasks() {
        Task task = new Task("Test Task", "Description", Status.NEW, duration, localDateTime);
        taskManager.createTask(task);

        Epic epic = new Epic("Test Epic", "Epic Description", duration, localDateTime.plusMinutes(60));
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Subtask Description", Status.IN_PROGRESS, epic.getId(), duration, localDateTime.plusMinutes(120));
        taskManager.createSubtask(subtask);

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
        Task task = new Task("Test Task", "Description", Status.NEW, duration, localDateTime);
        taskManager.createTask(task);

        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        Task loadedTask = loadedManager.getTaskById(task.getId()).get();
        assertEquals(Status.IN_PROGRESS, loadedTask.getStatus(), "Статус задачи не обновился");
    }

    @Test
    void shouldSaveStateAfterTaskDeletion() {
        Task task = new Task("Test Task", "Description", Status.NEW, duration, localDateTime);
        taskManager.createTask(task);

        taskManager.deleteTaskById(task.getId());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "Задача не была удалена");
    }

    @Test
    void shouldSaveAndLoadEmptyManager() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "Задачи не пусты.");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Эпики не пусты.");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Подзадачи не пусты.");
    }

    @Test
    void shouldThrowExceptionWhenFileNotFound() {
        File nonExistentFile = new File("non_existent_file.txt");
        assertThrows(ManagerLoadException.class, () -> FileBackedTaskManager.loadFromFile(nonExistentFile),
                "Должно быть исключение при загрузке из несуществующего файла.");
    }
}