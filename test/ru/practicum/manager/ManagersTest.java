package ru.practicum.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void shouldReturnNewTaskManagerInstanceEachTime() {
        TaskManager tm1 = Managers.getDefault();
        TaskManager tm2 = Managers.getDefault();

        assertNotNull(tm1);
        assertNotNull(tm2);
        assertNotSame(tm1, tm2, "Каждый вызов должен создавать новый менеджер");
    }

    @Test
    void shouldReturnNewHistoryManagerInstanceEachTime() {
        HistoryManager hm1 = Managers.getDefaultHistory();
        HistoryManager hm2 = Managers.getDefaultHistory();

        assertNotNull(hm1);
        assertNotNull(hm2);
        assertNotSame(hm1, hm2, "HistoryManager тоже должен быть новым каждый раз");
    }
}