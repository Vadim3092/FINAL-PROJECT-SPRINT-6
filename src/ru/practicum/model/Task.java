package ru.practicum.model;

import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status status;

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public String toCSVString() {
        return String.format("%d,%s,%s,%s,%s,", id, getType(), name, status, description);
    }

    public static Task fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String[] parts = value.split(",", -1);
        if (parts.length < 5) {
            return null;
        }
        try {
            int id = Integer.parseInt(parts[0]);
            TaskType type = TaskType.valueOf(parts[1].trim());
            String name = parts[2];
            Status status = Status.valueOf(parts[3].trim());
            String description = parts[4];

            if (type == TaskType.TASK) {
                return new Task(id, name, description, status);
            } else if (type == TaskType.EPIC) {
                return new Epic(id, name, description, status);
            } else if (type == TaskType.SUBTASK) {
                int epicId = parts.length > 5 && !parts[5].isEmpty() ? Integer.parseInt(parts[5]) : 0;
                return new Subtask(id, name, description, status, epicId);
            } else {
                return null;
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ru.practicum.model.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
