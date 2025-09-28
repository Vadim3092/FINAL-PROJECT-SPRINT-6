package ru.practicum.model;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Subtask> subtaskList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public void addSubtask(Subtask subtask) {
        if (subtask == null) return;
        subtaskList.add(subtask);
    }

    public void clearSubtasks() {
        subtaskList.clear();
    }

    public ArrayList<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void updateSubtask(Subtask oldSubtask, Subtask newSubtask) {
        subtaskList.remove(oldSubtask);
        subtaskList.add(newSubtask);
    }

    @Override
    public String toCSVString() {
        return String.format("%d,%s,%s,%s,%s,", id, TaskType.EPIC, name, status, description);
    }

    @Override
    public String toString() {
        return "ru.practicum.model.Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", subtaskList.size=" + subtaskList.size() +
                ", status=" + status +
                '}';
    }
}