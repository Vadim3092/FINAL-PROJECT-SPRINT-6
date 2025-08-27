package ru.practicum.manager;

import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;
    private int nextID = 1;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    private int getNextID() {
        return nextID++;
    }

    @Override
    public int addTask(Task task) {
        if (task == null) return -1;
        task.setId(getNextID());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        if (epic == null) return -1;
        epic.setId(getNextID());
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (subtask == null) {
            return -1;
        }

        int newId = getNextID();

        if (subtask.getEpicID() == newId) {
            nextID--;
            return -1;
        }

        subtask.setId(newId);
        if (!epics.containsKey(subtask.getEpicID())) {
            nextID--; //
            return -1;
        }

        subtasks.put(newId, subtask);
        Epic epic = epics.get(subtask.getEpicID());
        epic.addSubtask(subtask);
        updateEpicStatus(epic);
        return newId;
    }

    @Override
    public boolean updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) return false;
        tasks.put(task.getId(), task);
        return true;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) return false;
        Epic existing = epics.get(epic.getId());
        existing.setName(epic.getName());
        existing.setDescription(epic.getDescription());
        return true;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) return false;
        Subtask old = subtasks.get(subtask.getId());
        if (old.getEpicID() != subtask.getEpicID()) return false;

        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicID());
        epic.updateSubtask(old, subtask);
        updateEpicStatus(epic);
        return true;
    }

    @Override
    public Task getTaskByID(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        if (!epics.containsKey(epicId)) return Collections.emptyList();
        return new ArrayList<>(epics.get(epicId).getSubtaskList());
    }

    @Override
    public void deleteTasks() {
        removeTasksFromHistory(tasks.keySet());
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        Set<Integer> subtaskIds = new HashSet<>();
        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.getSubtaskList()) {
                subtaskIds.add(subtask.getId());
            }
        }

        removeTasksFromHistory(epics.keySet());
        removeTasksFromHistory(subtaskIds);

        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        removeTasksFromHistory(subtasks.keySet());
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public boolean deleteTaskByID(int id) {
        Task removed = tasks.remove(id);
        if (removed != null) {
            removeTaskFromHistory(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteEpicByID(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) return false;

        for (Subtask subtask : epic.getSubtaskList()) {
            subtasks.remove(subtask.getId());
            removeTaskFromHistory(subtask.getId());
        }

        removeTaskFromHistory(id);
        return true;
    }

    @Override
    public boolean deleteSubtaskByID(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return false;
        }

        Epic epic = epics.get(subtask.getEpicID());
        if (epic != null) {
            epic.getSubtaskList().remove(subtask);
            updateEpicStatus(epic);
        }

        removeTaskFromHistory(id);
        return true;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void removeTaskFromHistory(int id) {
        List<Task> history = historyManager.getHistory();
        history.removeIf(task -> task.getId() == id);
    }

    private void removeTasksFromHistory(Set<Integer> ids) {
        List<Task> history = historyManager.getHistory();
        history.removeIf(task -> ids.contains(task.getId()));
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> list = epic.getSubtaskList();
        if (list.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;
        for (Subtask s : list) {
            if (s.getStatus() != Status.NEW) allNew = false;
            if (s.getStatus() != Status.DONE) allDone = false;
        }

        if (allNew) epic.setStatus(Status.NEW);
        else if (allDone) epic.setStatus(Status.DONE);
        else epic.setStatus(Status.IN_PROGRESS);
    }
}