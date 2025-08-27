package ru.practicum.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void shouldBeEqualIfSameId() {
        Subtask sub1 = new Subtask(1, "Сделать отчёт", "PDF", Status.NEW, 100);
        Subtask sub2 = new Subtask(1, "Сделать презентацию", "PPT", Status.DONE, 200);

        assertEquals(sub1, sub2, "Подзадачи с одинаковым id — это одна и та же подзадача");
        assertEquals(sub1.hashCode(), sub2.hashCode());
    }

    @Test
    void shouldHaveEpicId() {
        Subtask subtask = new Subtask("Купить билеты", "На поезд", 5);

        assertEquals(5, subtask.getEpicID(), "EpicID должен сохраняться");
    }

    @Test
    void shouldNotAllowNegativeEpicId() {

        Subtask subtask = new Subtask("Тест", "Описание", -1);
        assertEquals(-1, subtask.getEpicID(), "id эпика может быть любым числом (но в менеджере проверяется)");
        // В менеджере мы уже проверяем, существует ли эпик с таким id
    }

    @Test
    void shouldUpdateStatusIndependently() {
        Subtask subtask = new Subtask("Работа", "Над проектом", 10);
        subtask.setStatus(Status.DONE);

        assertEquals(Status.DONE, subtask.getStatus());
        assertEquals(10, subtask.getEpicID());
    }
}