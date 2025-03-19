package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void subtasksWithSameIdShouldBeEqual() {
        Subtask task1 = new Subtask("Подзадача", "Описание", Status.NEW,1);
        task1.setId(1);

        Subtask task2 = new Subtask("Подзадача", "Описание", Status.NEW,1);
        task2.setId(1);

        assertEquals(task1, task2, "Подзадачи с одинаковым id должны быть равны");
    }

    @Test
    void subtasksWithDifferentSameIdShouldBeNotEqual() {
        Subtask task1 = new Subtask("Подзадача", "Описание", Status.NEW, 1);
        task1.setId(1);

        Subtask task2 = new Subtask("Подзадача", "Описание", Status.NEW,1);
        task2.setId(2);

        assertNotEquals(task1, task2, "Подзадачи с разным id должны быть не равны");
    }

    @Test
    void epicIdShouldBe1() {
        Subtask subtask = new Subtask("Подзадача", "Описание", Status.NEW, 1);
        assertEquals(1, subtask.getEpicId(), "EpicId должен быть 1");
    }

}