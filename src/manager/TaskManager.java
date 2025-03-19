package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    // Методы для работы с задачами
    Task createTask(Task task);

    ArrayList<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    void updateTask(Task task);

    void deleteTaskById(int id);

    // Методы для работы с подзадачами
    Subtask createSubtask(Subtask subtask);

    ArrayList<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Subtask getSubtaskById(int id);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int id);

    // Методы для работы с эпиками
    Epic createEpic(Epic epic);

    ArrayList<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(int id);

    void updateEpic(Epic epic);

    void deleteEpicById(int id);

    ArrayList<Subtask> getSubtasksByEpicId(int epicId);

    ArrayList<Task> getHistory();
}
