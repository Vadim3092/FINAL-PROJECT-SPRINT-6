package ru.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldAddAndReturnTaskById() {
        Task task = new Task("Купить хлеб", "Срочно");
        int id = taskManager.addTask(task);

        Task saved = taskManager.getTaskByID(id);

        assertNotNull(saved);
        assertEquals("Купить хлеб", saved.getName());
    }

    @Test
    void shouldAddAndReturnEpicById() {
        Epic epic = new Epic("Ремонт", "В квартире");
        int id = taskManager.addEpic(epic);

        Epic saved = taskManager.getEpicByID(id);

        assertNotNull(saved);
        assertEquals("Ремонт", saved.getName());
    }

    @Test
    void shouldAddSubtaskAndLinkToEpic() {
        Epic epic = new Epic("Проект", "Сайт");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Верстка", "Главная", epicId);
        int subId = taskManager.addSubtask(subtask);

        Subtask saved = taskManager.getSubtaskByID(subId);
        assertNotNull(saved);
        assertEquals(epicId, saved.getEpicID());
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task("Старое", "Описание");
        int id = taskManager.addTask(task);

        Task updated = new Task(id, "Новое", "Обновлённое", Status.DONE);
        boolean result = taskManager.updateTask(updated);

        assertTrue(result);
        assertEquals("Новое", taskManager.getTaskByID(id).getName());
    }

    @Test
    void shouldUpdateEpicStatusBasedOnSubtasks() {
        Epic epic = new Epic("Разработка", "App");
        int epicId = taskManager.addEpic(epic);

        Subtask s1 = new Subtask("UI", "Сделать", epicId);
        Subtask s2 = new Subtask("API", "Подключить", epicId);
        taskManager.addSubtask(s1);
        taskManager.addSubtask(s2);

        // Обновляем статус одной подзадачи
        s1.setStatus(Status.DONE);
        taskManager.updateSubtask(s1);

        Epic updatedEpic = taskManager.getEpicByID(epicId);
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus());
    }

    @Test
    void shouldGenerateUniqueIds() {
        Task t1 = new Task("1", "desc");
        Task t2 = new Task("2", "desc");

        int id1 = taskManager.addTask(t1);
        int id2 = taskManager.addTask(t2);

        assertEquals(1, id1);
        assertEquals(2, id2);
    }

    @Test
    void shouldDeleteTaskById() {
        Task task = new Task("Для удаления", "desc");
        int id = taskManager.addTask(task);

        boolean deleted = taskManager.deleteTaskByID(id);
        assertTrue(deleted);
        assertNull(taskManager.getTaskByID(id));
    }

    @Test
    void shouldAddToHistoryWhenGettingById() {
        Task task = new Task("Для истории", "desc");
        int id = taskManager.addTask(task);

        taskManager.getTaskByID(id);
        List<Task> history = taskManager.getHistory();

        assertFalse(history.isEmpty());
        assertEquals("Для истории", history.get(0).getName());
    }
}