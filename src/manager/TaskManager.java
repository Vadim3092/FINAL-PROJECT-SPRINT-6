package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.*;

public class TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private int nextID = 1;

    private int getNextID() {
        return nextID++;
    }

    public int addTask(Task task) {
        if (task == null) return -1;
        task.setId(getNextID());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public int addEpic(Epic epic) {
        if (epic == null) return -1;
        epic.setId(getNextID());
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

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

    public boolean updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) {
            return false;
        }
        tasks.put(task.getId(), task);
        return true;
    }

    public boolean updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            return false;
        }
        Epic existingEpic = epics.get(epic.getId());
        existingEpic.setName(epic.getName());
        existingEpic.setDescription(epic.getDescription());
        return true;
    }

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

    public Task getTaskByID(int id) {
        return tasks.get(id);
    }

    public Epic getEpicByID(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskByID(int id) {
        return subtasks.get(id);
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Subtask> getEpicSubtasks(int epicId) {
        if (!epics.containsKey(epicId)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(epics.get(epicId).getSubtaskList());
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            epic.setStatus(Status.NEW);
        }
    }

    public boolean deleteTaskByID(int id) {
        return tasks.remove(id) != null;
    }

    public boolean deleteEpicByID(int id) {
        Epic epic = epics.get(id);
        if (epic == null) return false;

        for (Subtask subtask : epic.getSubtaskList()) {
            subtasks.remove(subtask.getId());
        }
        epics.remove(id);
        return true;
    }

    public boolean deleteSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) return false;

        Epic epic = epics.get(subtask.getEpicID());
        if (epic == null) return false;

        subtasks.remove(id);
        epic.getSubtaskList().remove(subtask);
        updateEpicStatus(epic);
        return true;
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