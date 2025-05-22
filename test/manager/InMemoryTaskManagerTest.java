package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void shouldGetPrioritizedTasks() {
        Task task1 = new Task("Task 1", "Description", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        Task task2 = new Task("Task 2", "Description", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> prioritized = new ArrayList<>(taskManager.getPrioritizedTasks());
        assertEquals(2, prioritized.size(), "Неверное количество задач.");
        assertEquals(task2, prioritized.get(0), "Первая задача должна быть task2.");
        assertEquals(task1, prioritized.get(1), "Вторая задача должна быть task1.");
    }
}