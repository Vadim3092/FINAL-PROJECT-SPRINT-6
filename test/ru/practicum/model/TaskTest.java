package ru.practicum.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    void tasksWithSameIdAreEqual() {
        Task task1 = new Task(1, "Купить хлеб", "В магазине", Status.NEW, null, null);
        Task task2 = new Task(1, "Погулять", "С собакой", Status.DONE, null, null);

        assertEquals(task1, task2);
        assertEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void tasksWithDifferentIdAreNotEqual() {
        Task task1 = new Task(1, "Первая", "Описание", Status.NEW, null, null);
        Task task2 = new Task(2, "Первая", "Описание", Status.NEW, null, null);

        assertNotEquals(task1, task2);
    }
}