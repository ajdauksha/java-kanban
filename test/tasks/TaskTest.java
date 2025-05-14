package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest extends BaseTaskTest<Task> {

    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Задача", "Описание", Status.NEW, duration, localDateTime);
        task1.setId(1);

        Task task2 = new Task("Задача", "Описание", Status.NEW, duration, localDateTime.plusMinutes(60));
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }

    @Test
    void tasksWithDifferentSameIdShouldBeNotEqual() {
        Task task1 = new Task("Задача", "Описание", Status.NEW, duration, localDateTime);
        task1.setId(1);

        Task task2 = new Task("Задача", "Описание", Status.NEW, duration, localDateTime.plusMinutes(60));
        task2.setId(2);

        assertNotEquals(task1, task2, "Задачи с разным id должны быть не равны");
    }

}