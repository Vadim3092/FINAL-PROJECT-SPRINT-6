package ru.practicum.manager;

import ru.practicum.model.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager;
    protected int nextID = 1;

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>((t1, t2) -> {
        LocalDateTime start1 = t1.getStartTime();
        LocalDateTime start2 = t2.getStartTime();

        if (start1 == null && start2 == null) return Integer.compare(t1.getId(), t2.getId());
        if (start1 == null) return 1;
        if (start2 == null) return -1;
        int cmp = start1.compareTo(start2);
        return cmp != 0 ? cmp : Integer.compare(t1.getId(), t2.getId());
    });

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    private int getNextID() {
        return nextID++;
    }

    private void addTaskToPrioritized(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeTaskFromPrioritized(Task task) {
        prioritizedTasks.remove(task);
    }

    private boolean isIntersecting(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) return false;
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime end2 = task2.getEndTime();
        return !end1.isBefore(task2.getStartTime()) && !end2.isBefore(task1.getStartTime());
    }

    private boolean hasIntersections(Task newTask) {
        if (newTask.getStartTime() == null) return false;
        for (Task task : prioritizedTasks) {
            if (isIntersecting(newTask, task)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int addTask(Task task) {
        if (task == null || hasIntersections(task)) return -1;
        task.setId(getNextID());
        tasks.put(task.getId(), task);
        addTaskToPrioritized(task);
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
        if (subtask == null || !epics.containsKey(subtask.getEpicID()) || hasIntersections(subtask)) {
            return -1;
        }
        int newId = getNextID();
        subtask.setId(newId);
        subtasks.put(newId, subtask);
        Epic epic = epics.get(subtask.getEpicID());
        epic.addSubtask(subtask);
        updateEpicStatus(epic);
        addTaskToPrioritized(subtask);
        return newId;
    }

    @Override
    public boolean updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) return false;
        if (hasIntersections(task)) return false;
        Task old = tasks.get(task.getId());
        removeTaskFromPrioritized(old);
        tasks.put(task.getId(), task);
        addTaskToPrioritized(task);
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
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            return false;
        }
        if (hasIntersections(subtask)) {
            return false;
        }
        Subtask old = subtasks.get(subtask.getId());
        if (old.getEpicID() != subtask.getEpicID()) {
            return false;
        }

        removeTaskFromPrioritized(old);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicID());
        epic.updateSubtask(old, subtask);
        updateEpicStatus(epic);
        addTaskToPrioritized(subtask);
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
        return epics.getOrDefault(epicId, new Epic("", ""))
                .getSubtaskList()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTasks() {
        removeTasksFromHistory(tasks.keySet());
        for (Task task : tasks.values()) {
            removeTaskFromPrioritized(task);
        }
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

        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.getSubtaskList()) {
                removeTaskFromPrioritized(subtask);
            }
        }

        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        removeTasksFromHistory(subtasks.keySet());
        for (Subtask subtask : subtasks.values()) {
            removeTaskFromPrioritized(subtask);
        }
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
            removeTaskFromPrioritized(removed);
            removeTaskFromHistory(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteEpicByID(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) {
            return false;
        }

        for (Subtask subtask : epic.getSubtaskList()) {
            subtasks.remove(subtask.getId());
            removeTaskFromPrioritized(subtask);
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

        removeTaskFromPrioritized(subtask);

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

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void removeTaskFromHistory(int id) {
        historyManager.remove(id);
    }

    private void removeTasksFromHistory(Set<Integer> ids) {
        for (int id : ids) {
            historyManager.remove(id);
        }
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
            if (s.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (s.getStatus() != Status.DONE) {
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