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

    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldRemoveSubtaskFromEpicWhenDeleted() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание", epicId);
        int subtaskId = taskManager.addSubtask(subtask);

        List<Subtask> epicSubtasks = taskManager.getEpicSubtasks(epicId);
        assertEquals(1, epicSubtasks.size(), "В эпике должна быть 1 подзадача");

        boolean deleted = taskManager.deleteSubtaskByID(subtaskId);
        assertTrue(deleted, "Подзадача должна быть удалена");

        epicSubtasks = taskManager.getEpicSubtasks(epicId);
        assertEquals(0, epicSubtasks.size(), "После удаления подзадачи, список в эпике должен быть пуст");
    }

    @Test
    void shouldRemoveAllSubtasksWhenEpicIsDeleted() {
        Epic epic = new Epic("Эпик", "Описание");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", epicId);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", epicId);
        int id1 = taskManager.addSubtask(subtask1);
        int id2 = taskManager.addSubtask(subtask2);

        assertNotNull(taskManager.getSubtaskByID(id1), "Подзадача 1 должна существовать");
        assertNotNull(taskManager.getSubtaskByID(id2), "Подзадача 2 должна существовать");

        boolean deleted = taskManager.deleteEpicByID(epicId);
        assertTrue(deleted, "Эпик должен быть удалён");

        assertNull(taskManager.getSubtaskByID(id1), "Подзадача 1 должна быть удалена");
        assertNull(taskManager.getSubtaskByID(id2), "Подзадача 2 должна быть удалена");

        assertNull(taskManager.getEpicByID(epicId), "Эпик должен быть удалён");
    }

    @Test
    void shouldUpdateEpicStatusWhenSubtaskStatusChanges() {
        Epic epic = new Epic("Эпик", "Описание");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание", epicId);
        int subtaskId = taskManager.addSubtask(subtask);

        assertEquals(Status.NEW, taskManager.getEpicByID(epicId).getStatus(), "Эпик должен быть NEW");

        subtask.setStatus(Status.DONE);
        boolean updated = taskManager.updateSubtask(subtask);
        assertTrue(updated, "Подзадача должна обновиться");

        assertEquals(Status.DONE, taskManager.getEpicByID(epicId).getStatus(), "Эпик должен стать DONE");

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание", epicId);
        taskManager.addSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicByID(epicId).getStatus(), "Эпик должен стать IN_PROGRESS");
    }

    @Test
    void shouldRemoveDeletedTaskFromHistory() {
        Task task = new Task("Задача", "Описание");
        int taskId = taskManager.addTask(task);

        taskManager.getTaskByID(taskId);

        taskManager.deleteTaskByID(taskId);

        List<Task> history = taskManager.getHistory();
        for (Task t : history) {
            assertNotEquals(taskId, t.getId(), "Удалённая задача не должна оставаться в истории");
        }
    }

    @Test
    void shouldClearHistoryWhenDeletingAllTasks() {
        Task task1 = new Task("Задача 1", "Описание 1");
        Task task2 = new Task("Задача 2", "Описание 2");
        int id1 = taskManager.addTask(task1);
        int id2 = taskManager.addTask(task2);

        taskManager.getTaskByID(id1);
        taskManager.getTaskByID(id2);

        taskManager.deleteTasks();

        List<Task> history = taskManager.getHistory();
        for (Task t : history) {
            assertNotEquals(id1, t.getId(), "Задача 1 должна быть удалена из истории");
            assertNotEquals(id2, t.getId(), "Задача 2 должна быть удалена из истории");
        }
    }
}