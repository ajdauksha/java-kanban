package utils;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void defaultTaskManagerShouldBeInstanceOfInMemoryTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "TaskManager не должен быть null");
        assertInstanceOf(InMemoryTaskManager.class, taskManager, "TaskManager должен быть экземпляром TaskManager");
    }

    @Test
    void defaultHistoryManagerShouldBeInstanceOfInMemoryHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager should not be null");
        assertInstanceOf(InMemoryHistoryManager.class, historyManager, "HistoryManager должен быть экземпляром HistoryManager");
    }

}