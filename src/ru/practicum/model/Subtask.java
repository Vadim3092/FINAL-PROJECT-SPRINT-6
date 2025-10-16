package ru.practicum.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicID;

    public Subtask(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration, int epicID) {
        super(id, name, description, status, startTime, duration);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, int epicID, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, int epicID) {
        this(name, description, epicID, null, null);
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toCSVString() {
        String base = super.toCSVString();
        return base + "," + epicID;
    }

    @Override
    public String toString() {
        return "ru.practicum.model.Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", epicID=" + epicID +
                '}';
    }
}
