package ru.practicum.manager;

import ru.practicum.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
        loadDataFromFile(file);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        return new FileBackedTaskManager(file);
    }

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);
        save();
        return id;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean result = super.updateTask(task);
        save();
        return result;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        boolean result = super.updateEpic(epic);
        save();
        return result;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        boolean result = super.updateSubtask(subtask);
        save();
        return result;
    }

    @Override
    public boolean deleteTaskByID(int id) {
        boolean result = super.deleteTaskByID(id);
        save();
        return result;
    }

    @Override
    public boolean deleteEpicByID(int id) {
        boolean result = super.deleteEpicByID(id);
        save();
        return result;
    }

    @Override
    public boolean deleteSubtaskByID(int id) {
        boolean result = super.deleteSubtaskByID(id);
        save();
        return result;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,startTime,duration,epic\n");

            for (Task task : getTasks()) {
                writer.write(task.toCSVString() + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(epic.toCSVString() + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(subtask.toCSVString() + "\n");
            }

            List<Task> history = getHistory();
            if (!history.isEmpty()) {
                writer.write("\nHISTORY:\n");
                for (Task task : history) {
                    writer.write(task.getId() + "\n");
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить данные в файл", e);
        }
    }

    private void loadDataFromFile(File file) {
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            boolean inHistory = false;
            List<Integer> historyIds = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (line.equals("HISTORY:")) {
                    inHistory = true;
                    continue;
                }

                if (inHistory) {
                    try {
                        historyIds.add(Integer.parseInt(line));
                    } catch (NumberFormatException ignored) {
                    }
                    continue;
                }

                if (line.startsWith("id,")) {
                    continue;
                }

                Task task = Task.fromString(line);
                if (task == null) {
                    continue;
                }

                if (task instanceof Epic) {
                    epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    subtasks.put(subtask.getId(), subtask);
                    Epic epic = epics.get(subtask.getEpicID());
                    if (epic != null) {
                        epic.addSubtask(subtask);
                    }
                } else {
                    tasks.put(task.getId(), task);
                }

                if (task.getId() >= nextID) {
                    nextID = task.getId() + 1;
                }
            }

            for (int id : historyIds) {
                Task task = tasks.get(id);
                if (task != null) {
                    historyManager.add(task);
                    continue;
                }
                Epic epic = epics.get(id);
                if (epic != null) {
                    historyManager.add(epic);
                    continue;
                }
                Subtask subtask = subtasks.get(id);
                if (subtask != null) {
                    historyManager.add(subtask);
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось загрузить данные из файла", e);
        }
    }
}