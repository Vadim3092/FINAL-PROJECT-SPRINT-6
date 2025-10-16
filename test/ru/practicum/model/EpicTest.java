package ru.practicum.model;

import org.junit.jupiter.api.Test;
import ru.practicum.manager.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void epicsWithSameIdAreEqual() {
        Epic epic1 = new Epic(1, "Ремонт", "Квартира", Status.NEW);
        Epic epic2 = new Epic(1, "Отпуск", "Море", Status.DONE);

        assertEquals(epic1, epic2, "Эпики с одинаковым ID должны быть равны");
        assertEquals(epic1.hashCode(), epic2.hashCode(), "Хэшкоды должны совпадать");
    }

    @Test
    void subtaskCannotBeAddedIfEpicDoesNotExist() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Subtask invalidSubtask = new Subtask("Невалидная", "Нет эпика", 999);
        int subId = taskManager.addSubtask(invalidSubtask);

        assertEquals(-1, subId, "Подзадача с несуществующим epicID не должна добавляться");
    }

    @Test
    void validSubtaskIsAddedToEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Эпик", "Описание");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание", epicId);
        int subId = taskManager.addSubtask(subtask);

        assertNotEquals(-1, subId, "Подзадача должна быть добавлена");

        Epic savedEpic = taskManager.getEpicByID(epicId);
        assertEquals(1, savedEpic.getSubtaskList().size(), "Эпик должен содержать подзадачу");
        assertEquals(subId, savedEpic.getSubtaskList().get(0).getId());
    }
}