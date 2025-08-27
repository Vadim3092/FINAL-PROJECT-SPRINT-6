package ru.practicum.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    void getDefaultReturnsTaskManager() {
        // Проверяем, что возвращает не null
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);
    }

    @Test
    void getDefaultHistoryReturnsHistoryManager() {
        // Проверяем, что история тоже создаётся
        HistoryManager history = Managers.getDefaultHistory();
        assertNotNull(history);
    }
}