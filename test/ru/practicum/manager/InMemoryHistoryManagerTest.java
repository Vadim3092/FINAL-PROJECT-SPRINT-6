package ru.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Task;
import ru.practicum.model.Status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task = new Task(1, "Test Task", "Desc", Status.NEW);
    }

    @Test
    void shouldPreserveOriginalTaskData() {
        historyManager.add(task);
        task.setName("Changed Name");

        List<Task> history = historyManager.getHistory();
        assertEquals("Test Task", history.get(0).getName());
    }
}