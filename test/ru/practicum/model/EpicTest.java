package ru.practicum.model;

import org.junit.jupiter.api.Test;
import ru.practicum.manager.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @Test
    void epicsWithSameIdAreEqual() {
        // Эпики с одинаковым id должны быть равны
        Epic epic1 = new Epic(1, "Ремонт", "Квартира", Status.NEW);
        Epic epic2 = new Epic(1, "Отпуск", "Море", Status.DONE);

        assertEquals(epic1, epic2);
    }

    @Test
    void epicCannotHaveItselfAsSubtask() {

        InMemoryTaskManager taskManager = new InMemoryTaskManager();


        Epic myEpic = new Epic("Сам себе эпик", "Не должно работать");
        int epicId = taskManager.addEpic(myEpic);

        Subtask selfSubtask = new Subtask("Я — эпик?", "epicID = 1", epicId);

        int subId = taskManager.addSubtask(selfSubtask);

        Epic savedEpic = taskManager.getEpicByID(epicId);


        boolean found = false;
        for (Subtask s : savedEpic.getSubtaskList()) {
            if (s.getId() == subId) {
                found = false;
                break;
            }
        }

        assertFalse(found, "Эпик не должен содержать подзадачу, ссылающуюся на него");
    }
}