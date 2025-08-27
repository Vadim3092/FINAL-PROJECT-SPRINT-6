package ru.practicum.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    @Test
    void subtasksWithSameIdAreEqual() {

        Subtask sub1 = new Subtask(1, "Уборка", "Комната", Status.NEW, 100);
        Subtask sub2 = new Subtask(1, "Мусор", "Кухня", Status.DONE, 200);

        assertEquals(sub1, sub2);
    }

    @Test
    void subtaskStoresEpicID() {
        // Проверяем, что epicID сохранился
        Subtask sub = new Subtask("Тест", "Описание", 5);

        assertEquals(5, sub.getEpicID());
    }
}