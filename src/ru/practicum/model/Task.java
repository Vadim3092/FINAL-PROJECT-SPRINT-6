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

    public String toCSVString() {
        return String.format("%d,%s,%s,%s,%s,", id, TaskType.TASK, name, status, description);
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",", -1);
        if (parts.length < 5) return null;
        try {
            int id = Integer.parseInt(parts[0]);
            String type = parts[1];
            String name = parts[2];
            Status status = Status.valueOf(parts[3]);
            String description = parts[4];

            if ("TASK".equals(type)) {
                return new Task(id, name, description, status);
            } else if ("EPIC".equals(type)) {
                return new Epic(id, name, description, status);
            } else if ("SUBTASK".equals(type)) {
                int epicId = Integer.parseInt(parts[5]);
                return new Subtask(id, name, description, status, epicId);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
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
