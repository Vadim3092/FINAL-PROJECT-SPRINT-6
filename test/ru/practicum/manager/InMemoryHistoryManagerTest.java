package ru.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();

        task1 = new Task("Задача 1", "Описание 1");
        task1.setId(1);

        task2 = new Task("Задача 2", "Описание 2");
        task2.setId(2);

        task3 = new Task("Задача 3", "Описание 3");
        task3.setId(3);
    }

    @Test
    void testAddTask() {
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());

        assertEquals(task1, history.get(0));
    }

    @Test
    void testAddNullTaskDoesNothing() {
        historyManager.add(null);

        List<Task> history = historyManager.getHistory();

        assertEquals(0, history.size());
    }

    @Test
    void testAddSameTaskMovesItToEnd() {
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
    }

    @Test
    void testRemoveTask() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    void testRemoveFirstTask() {

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    void testRemoveLastTask() {
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void testRemoveNonExistentTask() {
        historyManager.add(task1);

        historyManager.remove(999);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void testHistoryLimitedTo10Tasks() {
        for (int i = 1; i <= 11; i++) {
            Task task = new Task("Задача " + i, "Описание " + i);
            task.setId(i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();

        assertEquals(10, history.size());

        boolean foundTask1 = false;
        for (Task task : history) {
            if (task.getId() == 1) {
                foundTask1 = true;
                break;
            }
        }
        assertFalse(foundTask1, "Задача с ID=1 должна быть удалена");

        boolean foundTask11 = false;
        for (Task task : history) {
            if (task.getId() == 11) {
                foundTask11 = true;
                break;
            }
        }
        assertTrue(foundTask11, "Задача с ID=11 должна быть в истории");
    }

    @Test
    void testEmptyHistoryInitially() {
        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    void testEmptyHistoryAfterRemovingAll() {
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(1);
        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    void testRemoveFromEmptyHistoryDoesNotCrash() {
        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size());
    }
}