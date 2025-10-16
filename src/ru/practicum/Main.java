package ru.practicum;

import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        File file = new File("tasks.csv");
        TaskManager taskManager = Managers.getFileBackedManager(file);

        System.out.println("Привет! Это улучшенный трекер задач с поддержкой времени и приоритетов.");
        System.out.println("=============================================================");

        System.out.println("\n1. Создаём задачи с указанием времени:");
        Task task1 = new Task("Позвонить клиенту", "Обсудить детали проекта",
                LocalDateTime.of(2024, 6, 10, 10, 0), Duration.ofMinutes(30));
        Task task2 = new Task("Подготовить презентацию", "Для встречи с инвесторами",
                LocalDateTime.of(2024, 6, 10, 11, 0), Duration.ofHours(2));

        int id1 = taskManager.addTask(task1);
        int id2 = taskManager.addTask(task2);

        System.out.println("Задача 1 (ID=" + id1 + "): " + task1.getName() +
                " | Начало: " + task1.getStartTime() + " | Конец: " + task1.getEndTime());
        System.out.println(" Задача 2 (ID=" + id2 + "): " + task2.getName() +
                " | Начало: " + task2.getStartTime() + " | Конец: " + task2.getEndTime());

        System.out.println("\n2. Пытаемся добавить задачу, пересекающуюся по времени:");
        Task conflictingTask = new Task("Срочный звонок", "Важный клиент",
                LocalDateTime.of(2024, 6, 10, 10, 20), Duration.ofMinutes(20));
        int conflictId = taskManager.addTask(conflictingTask);
        if (conflictId == -1) {
            System.out.println(" Задача НЕ добавлена — обнаружено пересечение по времени!");
        }

        System.out.println("\n3. Создаём эпик 'Подготовка к конференции':");
        Epic conference = new Epic("Подготовка к конференции", "Выступление 15 июня");
        int epicId = taskManager.addEpic(conference);

        Subtask sub1 = new Subtask("Написать доклад", "Основные тезисы",
                epicId, LocalDateTime.of(2024, 6, 10, 14, 0), Duration.ofHours(3));
        Subtask sub2 = new Subtask("Сделать слайды", "PowerPoint",
                epicId, LocalDateTime.of(2024, 6, 11, 9, 0), Duration.ofHours(2));

        taskManager.addSubtask(sub1);
        taskManager.addSubtask(sub2);

        Epic loadedEpic = taskManager.getEpicByID(epicId);
        System.out.println("Эпик создан! Статус: " + loadedEpic.getStatus());
        System.out.println("   Начало: " + loadedEpic.getStartTime());
        System.out.println("   Продолжительность: " + loadedEpic.getDuration().toHours() + " ч");
        System.out.println("   Окончание: " + loadedEpic.getEndTime());

        System.out.println("\n4. Список ВСЕХ задач по приоритету (по времени начала):");
        List<Task> prioritized = taskManager.getPrioritizedTasks();
        for (Task t : prioritized) {
            System.out.println(" - [" + t.getStartTime() + "] " + t.getName() +
                    " (" + t.getClass().getSimpleName() + ")");
        }

        System.out.println("\n5. Просматриваем задачи для истории:");
        taskManager.getTaskByID(id1);
        taskManager.getEpicByID(epicId);
        taskManager.getSubtaskByID(sub1.getId());

        System.out.println("История просмотров:");
        for (Task t : taskManager.getHistory()) {
            System.out.println(" - " + t.getName() + " (" + t.getClass().getSimpleName() + ")");
        }

        System.out.println("\n Все данные сохранены в файл 'tasks.csv'");
        System.out.println("Проверьте файл — там теперь есть startTime и duration!");
    }
}
