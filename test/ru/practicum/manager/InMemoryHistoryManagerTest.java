package ru.practicum.manager;

import org.junit.jupiter.api.Test;
import ru.practicum.model.Task;
import ru.practicum.model.Status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    @Test
    void removedTaskIsNotInHistory() {

        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();


        Task task = new Task(1, "Удалить позже", "Описание", Status.NEW);
        historyManager.add(task);


        List<Task> history = historyManager.getHistory();
        assertFalse(history.isEmpty());
        assertEquals(1, history.size());


        history.removeIf(t -> t.getId() == 1);


        assertTrue(history.isEmpty());
    }
}