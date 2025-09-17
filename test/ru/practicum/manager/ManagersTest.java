package ru.practicum.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefaultReturnsTaskManager() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "getDefault() должен возвращать рабочий TaskManager");
    }

    @Test
    void getDefaultHistoryReturnsHistoryManager() {
        HistoryManager history = Managers.getDefaultHistory();
        assertNotNull(history, "getDefaultHistory() должен возвращать рабочий HistoryManager");
    }
}