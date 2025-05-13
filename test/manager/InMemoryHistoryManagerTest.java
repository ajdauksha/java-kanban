package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    protected Duration duration;
    protected LocalDateTime localDateTime;

    @BeforeEach
    void setUp() {
        duration = Duration.ofMinutes(30);
        localDateTime = LocalDateTime.now();

        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task 1", "Test description 1", Status.NEW, duration, localDateTime);
        task1.setId(1);
        task2 = new Task("Task 2", "Test description 2", Status.NEW, duration, localDateTime.plusMinutes(60));
        task2.setId(2);
        task3 = new Task("Task 3", "Test description 3", Status.NEW, duration, localDateTime.plusMinutes(120));
        task3.setId(3);
    }

    @Test
    void add() {
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
        assertEquals(task1, history.get(0), "Задачи в истории не совпадают.");
    }

    @Test
    void addShouldNotAddDuplicateTasks() {
        historyManager.add(task1);
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать только одну запись при дублировании.");
    }

    @Test
    void addShouldMoveTaskToEndIfAlreadyExists() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task1);

        final List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "История должна содержать 3 задачи.");
        assertEquals(task1, history.get(2), "Повторно добавленная задача должна быть в конце.");
    }

    @Test
    void removeShouldDeleteTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());
        final List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать 2 задачи после удаления.");
        assertFalse(history.contains(task2), "История не должна содержать удаленную задачу.");
        assertTrue(history.contains(task1) && history.contains(task3),
                "История должна содержать оставшиеся задачи.");
    }

    @Test
    void getHistoryShouldReturnTasksInCorrectOrder() {
        historyManager.add(task3);
        historyManager.add(task2);
        historyManager.add(task1);

        final List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "История должна содержать 3 задачи.");
        assertEquals(task3, history.get(0), "Первая задача в истории не совпадает.");
        assertEquals(task2, history.get(1), "Вторая задача в истории не совпадает.");
        assertEquals(task1, history.get(2), "Третья задача в истории не совпадает.");
    }

}