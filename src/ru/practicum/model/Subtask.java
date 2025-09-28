package ru.practicum.model;

public class Subtask extends Task {

    private final int epicID;

    public Subtask(String name, String description, int epicID) {
        super(name, description);
        this.epicID = epicID;
    }

    public Subtask(int id, String name, String description, Status status, int epicID) {
        super(id, name, description, status);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toCSVString() {
        return String.format("%d,%s,%s,%s,%s,%d", id, TaskType.SUBTASK, name, status, description, epicID);
    }

    @Override
    public String toString() {
        return "ru.practicum.model.Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", epicID=" + epicID +
                ", status=" + status +
                '}';
    }
}
