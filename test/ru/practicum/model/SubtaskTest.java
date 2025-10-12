package ru.practicum.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    @Test
    void subtasksWithSameIdAreEqual() {
        Subtask sub1 = new Subtask(1, "Уборка", "Комната", Status.NEW, null, null, 100);
        Subtask sub2 = new Subtask(1, "Мусор", "Кухня", Status.DONE, null, null, 200);

        assertEquals(sub1, sub2);
        assertEquals(sub1.hashCode(), sub2.hashCode());
    }

    @Test
    void subtaskStoresEpicID() {
        Subtask sub = new Subtask("Тест", "Описание", 5);

        assertEquals(5, sub.getEpicID());
    }
}