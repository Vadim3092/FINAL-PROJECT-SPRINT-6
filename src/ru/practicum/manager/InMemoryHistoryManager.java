package ru.practicum.manager;

import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> history = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (task == null) return;

        Task copy = copyTask(task);
        if (copy == null) return;

        history.removeIf(t -> t.getId() == task.getId());

        if (history.size() >= MAX_HISTORY_SIZE) {
            history.removeFirst();
        }

        history.addLast(copy);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    private Task copyTask(Task task) {
        if (task == null) return null;

        if (task instanceof Subtask subtask) {
            return new Subtask(
                    subtask.getId(),
                    subtask.getName(),
                    subtask.getDescription(),
                    subtask.getStatus(),
                    subtask.getEpicID()
            );
        } else if (task instanceof Epic epic) {
            return new Epic(
                    epic.getId(),
                    epic.getName(),
                    epic.getDescription(),
                    epic.getStatus()
            );
        } else {
            return new Task(
                    task.getId(),
                    task.getName(),
                    task.getDescription(),
                    task.getStatus()
            );
        }
    }
}
