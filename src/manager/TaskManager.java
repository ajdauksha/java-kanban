package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;
import java.util.Optional;

public interface TaskManager {
    // Методы для работы с задачами
    Task createTask(Task task);

    List<Task> getAllTasks();

    void deleteAllTasks();

    Optional<Task> getTaskById(int id);

    void updateTask(Task task);

    void deleteTaskById(int id);

    // Методы для работы с подзадачами
    Subtask createSubtask(Subtask subtask);

    List<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Optional<Subtask> getSubtaskById(int id);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int id);

    // Методы для работы с эпиками
    Epic createEpic(Epic epic);

    List<Epic> getAllEpics();

    void deleteAllEpics();

    Optional<Epic> getEpicById(int id);

    void updateEpic(Epic epic);

    void deleteEpicById(int id);

    List<Subtask> getSubtasksByEpicId(int epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
