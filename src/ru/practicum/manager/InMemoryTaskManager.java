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
        if (subtask == null) return -1;
        int epicId = subtask.getEpicID();
        if (!epics.containsKey(epicId)) return -1;

        subtask.setId(getNextID());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(epicId);
        epic.addSubtask(subtask);
        updateEpicStatus(epic);
        return subtask.getId();
    }

    @Override
    public boolean updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) {
            return false;
        }
        tasks.put(task.getId(), task);
        return true;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            return false;
        }
        Epic existingEpic = epics.get(epic.getId());
        existingEpic.setName(epic.getName());
        existingEpic.setDescription(epic.getDescription());
        return true;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            return false;
        }
        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask.getEpicID() != subtask.getEpicID()) {
            return false;
        }

        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicID());
        epic.updateSubtask(oldSubtask, subtask);
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
        if (!epics.containsKey(epicId)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(epics.get(epicId).getSubtaskList());
    }

    @Override
    public void deleteTasks() {
        tasks.clear();

        removeTasksFromHistory(tasks.keySet());
    }

    @Override
    public void deleteEpics() {

        Set<Integer> subtaskIdsToRemove = new HashSet<>();
        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.getSubtaskList()) {
                subtaskIdsToRemove.add(subtask.getId());
            }
        }


        removeTasksFromHistory(epics.keySet());
        removeTasksFromHistory(subtaskIdsToRemove);

        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        // Удаляем подзадачи из истории
        removeTasksFromHistory(subtasks.keySet());

        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public boolean deleteTaskByID(int id) {
        Task removedTask = tasks.remove(id);
        if (removedTask != null) {
            removeTaskFromHistory(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteEpicByID(int id) {
        Epic epic = epics.get(id);
        if (epic == null) return false;


        for (Subtask subtask : epic.getSubtaskList()) {
            removeTaskFromHistory(subtask.getId());
            subtasks.remove(subtask.getId());
        }


        removeTaskFromHistory(id);
        epics.remove(id);
        return true;
    }

    @Override
    public boolean deleteSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) return false;

        Epic epic = epics.get(subtask.getEpicID());
        if (epic == null) return false;

        subtasks.remove(id);
        epic.getSubtaskList().remove(subtask);
        updateEpicStatus(epic);


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
        List<Subtask> subtaskList = epic.getSubtaskList();
        if (subtaskList.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Subtask subtask : subtaskList) {
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}