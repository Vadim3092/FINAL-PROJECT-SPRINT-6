package ru.practicum.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void shouldBeEqualIfSameId() {
        Epic epic1 = new Epic(1, "Ремонт", "В квартире", Status.NEW);
        Epic epic2 = new Epic(1, "Отпуск", "На море", Status.DONE);

        assertEquals(epic1, epic2, "Эпики с одинаковым id — один и тот же эпик");
        assertEquals(epic1.hashCode(), epic2.hashCode());
    }

    @Test
    void shouldStartWithEmptySubtasks() {
        Epic epic = new Epic("Проект", "Большой");

        assertTrue(epic.getSubtaskList().isEmpty(), "У нового эпика нет подзадач");
    }

    @Test
    void shouldAddSubtask() {
        Epic epic = new Epic("Сайт", "Для компании");
        Subtask subtask = new Subtask("Верстка", "Главная страница", epic.getId());

        epic.addSubtask(subtask);

        assertEquals(1, epic.getSubtaskList().size());
        assertEquals(subtask, epic.getSubtaskList().get(0));
    }

    @Test
    void shouldUpdateSubtaskInList() {
        Epic epic = new Epic("Разработка", "v1.0");
        Subtask old = new Subtask(1, "Анализ", "Требований", Status.NEW, epic.getId());
        Subtask updated = new Subtask(1, "Анализ", "Готов", Status.DONE, epic.getId());

        epic.addSubtask(old);
        epic.updateSubtask(old, updated);

        assertEquals(1, epic.getSubtaskList().size());
        assertEquals(Status.DONE, epic.getSubtaskList().get(0).getStatus());
    }

    @Test
    void shouldClearSubtasks() {
        Epic epic = new Epic("Тест", "Описание");
        epic.addSubtask(new Subtask("1", "desc", epic.getId()));
        epic.addSubtask(new Subtask("2", "desc", epic.getId()));

        epic.clearSubtasks();

        assertTrue(epic.getSubtaskList().isEmpty());
    }

    @Test
    void shouldUpdateStatusBasedOnSubtasks() {
        Epic epic = new Epic("Разработка", "Мобильное приложение");


        Subtask s1 = new Subtask(1, "UI", "Сделать экраны", Status.NEW, epic.getId());
        Subtask s2 = new Subtask(2, "API", "Подключить", Status.DONE, epic.getId());

        epic.addSubtask(s1);
        epic.addSubtask(s2);


        updateEpicStatus(epic);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }


    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasks = epic.getSubtaskList();
        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Subtask s : subtasks) {
            if (s.getStatus() != Status.NEW) allNew = false;
            if (s.getStatus() != Status.DONE) allDone = false;
        }

        if (allNew) epic.setStatus(Status.NEW);
        else if (allDone) epic.setStatus(Status.DONE);
        else epic.setStatus(Status.IN_PROGRESS);
    }
}