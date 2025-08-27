package ru.practicum.manager;

import org.junit.jupiter.api.Test;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private final TaskManager taskManager = new InMemoryTaskManager();

    @Test
    void updatingSubtaskChangesItButKeepsEpicID() {
        // Сначала создаём эпик
        Epic epic = new Epic("Родитель", "Для подзадач");
        int epicId = taskManager.addEpic(epic);


        Subtask subtask = new Subtask("Старое имя", "Описание", epicId);
        int subId = taskManager.addSubtask(subtask);


        Subtask saved = taskManager.getSubtaskByID(subId);
        assertNotNull(saved);
        assertEquals("Старое имя", saved.getName());


        Subtask updated = new Subtask(subId, "Новое имя", "Обновлённое", Status.DONE, epicId);
        boolean result = taskManager.updateSubtask(updated);

        assertTrue(result, "Обновление должно пройти успешно");


        Subtask after = taskManager.getSubtaskByID(subId);
        assertEquals("Новое имя", after.getName());
        assertEquals(Status.DONE, after.getStatus());
        assertEquals(epicId, after.getEpicID()); // epicID не должен измениться
    }
}