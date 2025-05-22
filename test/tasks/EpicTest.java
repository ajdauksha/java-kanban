package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest extends BaseTaskTest<Epic> {

    @Test
    void epicsWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic("Эпик", "Описание", duration, localDateTime);
        epic1.setId(1);

        Epic epic2 = new Epic("Эпик", "Описание", duration, localDateTime.plusMinutes(60));
        epic2.setId(1);

        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");
    }

    @Test
    void epicsWithDifferentSameIdShouldBeNotEqual() {
        Epic epic1 = new Epic("Эпик", "Описание", duration, localDateTime);
        epic1.setId(1);

        Epic epic2 = new Epic("Эпик", "Описание", duration, localDateTime.plusMinutes(60));
        epic2.setId(2);

        assertNotEquals(epic1, epic2, "Эпики с разным id должны быть не равны");
    }

}