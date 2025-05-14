package manager;

import exceptions.ManagerOverlapException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    protected Duration duration;
    protected LocalDateTime localDateTime;

    @BeforeEach
    public void setUp() {
        taskManager = createTaskManager();
        duration = Duration.ofMinutes(30);
        localDateTime = LocalDateTime.now();
    }

    @Test
    void shouldCreateTask() {
        Task task = new Task("Test Task", "Description", Status.NEW, duration, localDateTime);
        final Task savedTask = taskManager.createTask(task);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldCreateEpic() {
        Epic epic = new Epic("Test Epic", "Description", duration, localDateTime);
        final Epic savedEpic = taskManager.createEpic(epic);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void shouldCreateSubtask() {
        Epic epic = new Epic("Test Epic", "Description", duration, localDateTime);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Description", Status.NEW, epic.getId(), duration, localDateTime);
        final Subtask savedSubtask = taskManager.createSubtask(subtask);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void shouldReturnEmptyListWhenNoTasks() {
        assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач не пуст.");
    }

    @Test
    void shouldReturnEmptyListWhenNoEpics() {
        assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков не пуст.");
    }

    @Test
    void shouldReturnEmptyListWhenNoSubtasks() {
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Список подзадач не пуст.");
    }

    @Test
    void shouldDeleteAllTasks() {
        Task task1 = new Task("Task 1", "Description", Status.NEW, duration, localDateTime);
        Task task2 = new Task("Task 2", "Description", Status.IN_PROGRESS, duration, localDateTime.plusMinutes(60));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.deleteAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty(), "Задачи не удалены.");
    }

    @Test
    void shouldDeleteAllEpics() {
        Epic epic1 = new Epic("Epic 1", "Description", duration, localDateTime);
        Epic epic2 = new Epic("Epic 2", "Description", duration, localDateTime.plusMinutes(60));
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        taskManager.deleteAllEpics();

        assertTrue(taskManager.getAllEpics().isEmpty(), "Эпики не удалены.");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи не удалены.");
    }

    @Test
    void shouldDeleteAllSubtasks() {
        Epic epic = new Epic("Epic", "Description", duration, localDateTime);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", Status.NEW, epic.getId(), duration, localDateTime.plusMinutes(60));
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Status.DONE, epic.getId(), duration, localDateTime.plusMinutes(120));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.deleteAllSubtasks();

        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи не удалены.");
    }

    @Test
    void shouldGetTaskById() {
        Task task = new Task("Task", "Description", Status.NEW, duration, localDateTime);
        taskManager.createTask(task);

        assertEquals(task, taskManager.getTaskById(task.getId()).get(), "Задача не найдена.");
    }

    @Test
    void shouldGetEpicById() {
        Epic epic = new Epic("Epic", "Description", duration, localDateTime);
        taskManager.createEpic(epic);

        assertEquals(epic, taskManager.getEpicById(epic.getId()).get(), "Эпик не найден.");
    }

    @Test
    void shouldGetSubtaskById() {
        Epic epic = new Epic("Epic", "Description", duration, localDateTime);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", Status.NEW, epic.getId(), duration, localDateTime);
        taskManager.createSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()).get(), "Подзадача не найдена.");
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task("Task", "Description", Status.NEW, duration, localDateTime);
        taskManager.createTask(task);
        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);

        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(task.getId()).get().getStatus(),
                "Статус задачи не обновлен.");
    }

    @Test
    void shouldUpdateEpic() {
        Epic epic = new Epic("Epic", "Description", duration, localDateTime);
        taskManager.createEpic(epic);
        epic.setDescription("New Description");
        taskManager.updateEpic(epic);

        assertEquals("New Description", taskManager.getEpicById(epic.getId()).get().getDescription(),
                "Описание эпика не обновлено.");
    }

    @Test
    void shouldUpdateSubtask() {
        Epic epic = new Epic("Epic", "Description", duration, localDateTime);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", Status.NEW, epic.getId(), duration, localDateTime.plusMinutes(60));
        taskManager.createSubtask(subtask);
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);

        assertEquals(Status.DONE, taskManager.getSubtaskById(subtask.getId()).get().getStatus(),
                "Статус подзадачи не обновлен.");
    }

    @Test
    void shouldDeleteTaskById() {
        Task task = new Task("Task", "Description", Status.NEW, duration, localDateTime);
        taskManager.createTask(task);
        taskManager.deleteTaskById(task.getId());

        assertTrue(taskManager.getTaskById(task.getId()).isEmpty(), "Задача не удалена.");
    }

    @Test
    void shouldDeleteEpicById() {
        Epic epic = new Epic("Epic", "Description", duration, localDateTime);
        taskManager.createEpic(epic);
        taskManager.deleteEpicById(epic.getId());

        assertTrue(taskManager.getEpicById(epic.getId()).isEmpty(), "Эпик не удален.");
    }

    @Test
    void shouldDeleteSubtaskById() {
        Epic epic = new Epic("Epic", "Description", duration, localDateTime);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", Status.NEW, epic.getId(), duration, localDateTime.plusMinutes(60));
        taskManager.createSubtask(subtask);
        taskManager.deleteSubtaskById(subtask.getId());

        assertTrue(taskManager.getSubtaskById(subtask.getId()).isEmpty(), "Подзадача не удалена.");
    }

    @Test
    void shouldGetSubtasksByEpicId() {
        Epic epic = new Epic("Epic", "Description", duration, localDateTime);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", Status.NEW, epic.getId(), duration, localDateTime.plusMinutes(60));
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Status.DONE, epic.getId(), duration, localDateTime.plusMinutes(120));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epic.getId());
        assertEquals(2, subtasks.size(), "Неверное количество подзадач.");
        assertTrue(subtasks.contains(subtask1), "Подзадача 1 не найдена.");
        assertTrue(subtasks.contains(subtask2), "Подзадача 2 не найдена.");
    }

    @Test
    void shouldCalculateEpicStatusWhenAllSubtasksNew() {
        Epic epic = new Epic("Epic", "Description", duration, localDateTime);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", Status.NEW, epic.getId(), duration, localDateTime.plusMinutes(60));
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Status.NEW, epic.getId(), duration, localDateTime.plusMinutes(120));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.NEW, taskManager.getEpicById(epic.getId()).get().getStatus(),
                "Статус эпика должен быть NEW.");
    }

    @Test
    void shouldCalculateEpicStatusWhenAllSubtasksDone() {
        Epic epic = new Epic("Epic", "Description", duration, localDateTime);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", Status.DONE, epic.getId(), duration, localDateTime.plusMinutes(60));
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Status.DONE, epic.getId(), duration, localDateTime.plusMinutes(120));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).get().getStatus(),
                "Статус эпика должен быть DONE.");
    }

    @Test
    void shouldCalculateEpicStatusWhenSubtasksNewAndDone() {
        Epic epic = new Epic("Epic", "Description", duration, localDateTime);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", Status.NEW, epic.getId(), duration, localDateTime.plusMinutes(60));
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Status.DONE, epic.getId(), duration, localDateTime.plusMinutes(120));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).get().getStatus(),
                "Статус эпика должен быть IN_PROGRESS.");
    }

    @Test
    void shouldCalculateEpicStatusWhenSubtasksInProgress() {
        Epic epic = new Epic("Epic", "Description", duration, localDateTime);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", Status.IN_PROGRESS, epic.getId(), duration, localDateTime.plusMinutes(60));
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Status.IN_PROGRESS, epic.getId(), duration, localDateTime.plusMinutes(120));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).get().getStatus(),
                "Статус эпика должен быть IN_PROGRESS.");
    }

    @Test
    void shouldCalculateEpicStatusWhenNoSubtasks() {
        Epic epic = new Epic("Epic", "Description", duration, localDateTime);
        taskManager.createEpic(epic);

        assertEquals(Status.NEW, taskManager.getEpicById(epic.getId()).get().getStatus(),
                "Статус эпика должен быть NEW.");
    }

    @Test
    void shouldPreventTaskTimeOverlap() {
        Task task1 = new Task("Task 1", "Description", Status.NEW,
                duration, localDateTime);
        taskManager.createTask(task1);

        Task task2 = new Task("Task 2", "Description", Status.NEW,
                duration, localDateTime.plusMinutes(10));

        assertThrows(ManagerOverlapException.class, () -> taskManager.createTask(task2),
                "Должно быть исключение при пересечении времени задач.");
    }

    @Test
    void shouldGetHistory() {
        Task task = new Task("Task", "Description", Status.NEW, duration, localDateTime);
        Epic epic = new Epic("Epic", "Description", duration, localDateTime.plusMinutes(60));
        taskManager.createTask(task);
        taskManager.createEpic(epic);

        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "Неверное количество задач в истории.");
        assertEquals(task, history.get(0), "Первая задача в истории не совпадает.");
        assertEquals(epic, history.get(1), "Вторая задача в истории не совпадает.");
    }
}