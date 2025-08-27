package ru.practicum.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void shouldBeEqualIfSameId() {
        Task task1 = new Task(1, "Почистить зубы", "С утра", Status.NEW);
        Task task2 = new Task(1, "Помыть лицо", "Тоже утром", Status.DONE);

        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
        assertEquals(task1.hashCode(), task2.hashCode(), "Хэш-коды должны совпадать при одинаковом id");
    }

    @Test
    void shouldNotBeEqualIfDifferentId() {
        Task task1 = new Task(1, "A", "Desc", Status.NEW);
        Task task2 = new Task(2, "A", "Desc", Status.NEW);

        assertNotEquals(task1, task2, "Разные id — разные задачи");
    }

    @Test
    void shouldCreateTaskWithAutoId() {
        Task task = new Task("Автозадача", "Описание");
        task.setId(42); // как будто менеджер присвоил

        assertEquals(42, task.getId());
        assertEquals("Автозадача", task.getName());
        assertEquals(Status.NEW, task.getStatus());
    }

    @Test
    void shouldUpdateTaskFields() {
        Task task = new Task("Старая", "Описание");
        task.setId(1);

        task.setName("Новая");
        task.setDescription("Обновлённое описание");
        task.setStatus(Status.IN_PROGRESS);

        assertEquals("Новая", task.getName());
        assertEquals("Обновлённое описание", task.getDescription());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }
}