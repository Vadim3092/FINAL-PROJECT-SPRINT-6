package ru.practicum.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File tempFile;

    @BeforeEach
    void beforeEach() {
        assertDoesNotThrow(() -> {
            tempFile = Files.createTempFile("tasks", ".csv").toFile();
            taskManager = new FileBackedTaskManager(tempFile);
        });
    }

    @AfterEach
    void afterEach() {
        if (tempFile != null && tempFile.exists()) {
            assertTrue(tempFile.delete(), "Не удалось удалить временный файл");
        }
    }

    @Test
    void shouldSaveAndLoadTasksCorrectly() {
        Task task = new Task("Сохранённая задача", "Описание");
        int taskId = taskManager.addTask(task);

        Epic epic = new Epic("Сохранённый эпик", "Описание эпика");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Сохранённая подзадача", "Описание подзадачи", epicId);
        int subtaskId = taskManager.addSubtask(subtask);

        taskManager.getTaskByID(taskId);
        taskManager.getSubtaskByID(subtaskId);

        taskManager.save();

        FileBackedTaskManager reloadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, reloadedManager.getTasks().size());
        assertEquals(1, reloadedManager.getEpics().size());
        assertEquals(1, reloadedManager.getSubtasks().size());

        Task loadedTask = reloadedManager.getTasks().get(0);
        assertEquals(taskId, loadedTask.getId());
        assertEquals("Сохранённая задача", loadedTask.getName());

        Epic loadedEpic = reloadedManager.getEpics().get(0);
        assertEquals(epicId, loadedEpic.getId());
        assertEquals("Сохранённый эпик", loadedEpic.getName());

        Subtask loadedSubtask = reloadedManager.getSubtasks().get(0);
        assertEquals(subtaskId, loadedSubtask.getId());
        assertEquals("Сохранённая подзадача", loadedSubtask.getName());
        assertEquals(epicId, loadedSubtask.getEpicID());

        List<Task> history = reloadedManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать ровно 2 элемента");
        assertEquals(taskId, history.get(0).getId());
        assertEquals(subtaskId, history.get(1).getId());
    }

    @Test
    void shouldHandleEmptyFileCorrectly() {
        assertDoesNotThrow(() -> {
            Files.write(tempFile.toPath(), new byte[0]); // очищаем файл
            FileBackedTaskManager emptyManager = FileBackedTaskManager.loadFromFile(tempFile);
            assertNotNull(emptyManager);
            assertTrue(emptyManager.getTasks().isEmpty());
            assertTrue(emptyManager.getEpics().isEmpty());
            assertTrue(emptyManager.getSubtasks().isEmpty());
            assertTrue(emptyManager.getHistory().isEmpty());
        });
    }

    @Test
    void shouldThrowManagerSaveExceptionOnUnwritableFile() {
        assertDoesNotThrow(() -> {
            File protectedFile = new File("protected_tasks.csv");
            protectedFile.createNewFile();
            protectedFile.setReadOnly();

            try {
                FileBackedTaskManager badManager = new FileBackedTaskManager(protectedFile);
                Task task = new Task("Test", "Desc");
                badManager.addTask(task);
                fail("Ожидалось ManagerSaveException");
            } catch (ManagerSaveException e) {

            } finally {
                if (!protectedFile.setWritable(true)) {
                }
                protectedFile.delete();
            }
        });
    }
}