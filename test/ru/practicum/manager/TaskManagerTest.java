package ru.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    @BeforeEach
    abstract void beforeEach();


    @Test
    void shouldSetEpicStatusToNew_WhenAllSubtasksAreNew() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.addEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc1", epicId);
        Subtask sub2 = new Subtask("Sub2", "Desc2", epicId);
        taskManager.addSubtask(sub1);
        taskManager.addSubtask(sub2);

        Epic updatedEpic = taskManager.getEpicByID(epicId);
        assertEquals(Status.NEW, updatedEpic.getStatus());
    }

    @Test
    void shouldSetEpicStatusToDone_WhenAllSubtasksAreDone() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.addEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc1", epicId);
        sub1.setStatus(Status.DONE);
        Subtask sub2 = new Subtask("Sub2", "Desc2", epicId);
        sub2.setStatus(Status.DONE);
        taskManager.addSubtask(sub1);
        taskManager.addSubtask(sub2);

        Epic updatedEpic = taskManager.getEpicByID(epicId);
        assertEquals(Status.DONE, updatedEpic.getStatus());
    }

    @Test
    void shouldSetEpicStatusToInProgress_WhenSubtasksAreMixed() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.addEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc1", epicId);
        sub1.setStatus(Status.NEW);
        Subtask sub2 = new Subtask("Sub2", "Desc2", epicId);
        sub2.setStatus(Status.DONE);
        taskManager.addSubtask(sub1);
        taskManager.addSubtask(sub2);

        Epic updatedEpic = taskManager.getEpicByID(epicId);
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus());
    }

    @Test
    void shouldSetEpicStatusToInProgress_WhenAnySubtaskIsInProgress() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.addEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc1", epicId);
        sub1.setStatus(Status.IN_PROGRESS);
        taskManager.addSubtask(sub1);

        Epic updatedEpic = taskManager.getEpicByID(epicId);
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus());
    }

    @Test
    void shouldNotAddSubtaskWithoutExistingEpic() {
        Subtask subtask = new Subtask("Sub", "Desc", 999); // несуществующий ID
        int id = taskManager.addSubtask(subtask);
        assertEquals(-1, id);
    }

    @Test
    void shouldNotAddTaskWithIntersectingTime() {
        Task task1 = new Task("Task1", "Desc1",
                LocalDateTime.of(2024, 6, 10, 10, 0), Duration.ofMinutes(30));
        taskManager.addTask(task1);

        Task task2 = new Task("Task2", "Desc2",
                LocalDateTime.of(2024, 6, 10, 10, 20), Duration.ofMinutes(20)); // пересекается

        int id = taskManager.addTask(task2);
        assertEquals(-1, id);
    }

    @Test
    void shouldAddNonIntersectingTasks() {
        Task task1 = new Task("Task1", "Desc1",
                LocalDateTime.of(2024, 6, 10, 10, 0), Duration.ofMinutes(30));
        int id1 = taskManager.addTask(task1);
        assertNotEquals(-1, id1);

        Task task2 = new Task("Task2", "Desc2",
                LocalDateTime.of(2024, 6, 10, 11, 0), Duration.ofMinutes(30)); // не пересекается

        int id2 = taskManager.addTask(task2);
        assertNotEquals(-1, id2);
    }

    @Test
    void shouldReturnEmptyHistory_WhenNoTasksViewed() {
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void shouldNotDuplicateTasksInHistory() {
        Task task = new Task("Task", "Desc");
        int id = taskManager.addTask(task);
        taskManager.getTaskByID(id);
        taskManager.getTaskByID(id); // повторный вызов

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void shouldRemoveTaskFromHistoryWhenDeleted() {
        Task task = new Task("Task", "Desc");
        int id = taskManager.addTask(task);
        taskManager.getTaskByID(id);
        taskManager.deleteTaskByID(id);

        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void shouldRemoveEpicAndSubtasksFromHistoryOnEpicDeletion() {
        Epic epic = new Epic("Epic", "Desc");
        int epicId = taskManager.addEpic(epic);

        Subtask sub = new Subtask("Sub", "Desc", epicId);
        int subId = taskManager.addSubtask(sub);

        taskManager.getEpicByID(epicId);
        taskManager.getSubtaskByID(subId);

        taskManager.deleteEpicByID(epicId);

        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }
}
