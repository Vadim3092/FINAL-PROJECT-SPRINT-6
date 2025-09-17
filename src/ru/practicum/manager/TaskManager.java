package ru.practicum.manager;

import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.List;

public interface TaskManager {
    int addTask(Task task);

    int addEpic(Epic epic);

    int addSubtask(Subtask subtask);

    boolean updateTask(Task task);

    boolean updateEpic(Epic epic);

    boolean updateSubtask(Subtask subtask);

    Task getTaskByID(int id);

    Epic getEpicByID(int id);

    Subtask getSubtaskByID(int id);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Subtask> getEpicSubtasks(int epicId);

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    boolean deleteTaskByID(int id);

    boolean deleteEpicByID(int id);

    boolean deleteSubtaskByID(int id);

    List<Task> getHistory();
}
