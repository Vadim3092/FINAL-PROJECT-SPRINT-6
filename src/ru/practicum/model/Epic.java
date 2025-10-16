package ru.practicum.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Subtask> subtaskList = new ArrayList<>();

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status, null, null);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
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
    public LocalDateTime getStartTime() {
        if (subtaskList.isEmpty()) {
            return null;
        }
        return subtaskList.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public Duration getDuration() {
        if (subtaskList.isEmpty()) {
            return null;
        }
        long totalMinutes = subtaskList.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .mapToLong(Duration::toMinutes)
                .sum();
        return Duration.ofMinutes(totalMinutes);
    }

    @Override
    public LocalDateTime getEndTime() {
        if (subtaskList.isEmpty()) {
            return null;
        }
        return subtaskList.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toCSVString() {
        String startTimeStr = (getStartTime() != null) ? getStartTime().toString() : "";
        String durationStr = (getDuration() != null) ? String.valueOf(getDuration().toMinutes()) : "";
        return String.format("%d,%s,%s,%s,%s,%s,%s", id, getType(), name, status, description, startTimeStr, durationStr);
    }

    @Override
    public String toString() {
        return "ru.practicum.model.Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", subtaskList.size=" + subtaskList.size() +
                ", status=" + status +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                '}';
    }
}