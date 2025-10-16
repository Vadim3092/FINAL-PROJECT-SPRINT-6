package ru.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task1", "Desc1");
        task1.setId(1);
        task2 = new Task("Task2", "Desc2");
        task2.setId(2);
        task3 = new Task("Task3", "Desc3");
        task3.setId(3);
    }

    @Test
    void shouldReturnEmptyHistory_WhenNoTasksAdded() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldAddTasksToHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void shouldNotDuplicateTasksInHistory() {
        historyManager.add(task1);
        historyManager.add(task1);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void shouldRemoveTaskFromBeginningOfHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(1); // удаляем первый
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    void shouldRemoveTaskFromMiddleOfHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(2); // удаляем средний
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    void shouldRemoveTaskFromEndOfHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(3); // удаляем последний
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(1));
    }

    @Test
    void shouldRespectMaxHistorySize() {
        for (int i = 1; i <= 12; i++) {
            Task t = new Task("Task" + i, "Desc");
            t.setId(i);
            historyManager.add(t);
        }
        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size()); // MAX_HISTORY_SIZE = 10
        assertEquals(3, history.get(0).getId()); // первые 2 удалены
    }
}