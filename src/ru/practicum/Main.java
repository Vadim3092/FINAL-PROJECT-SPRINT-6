package ru.practicum;

import ru.practicum.manager.InMemoryTaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

public class Main {

    public static void main(String[] args) {


        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        System.out.println("Привет! Это трекер задач.");
        System.out.println("Сейчас будем создавать задачи и смотреть как работает история!");
        System.out.println("=============================================================");


        System.out.println("\n1. Создаем задачу 'Помыть полы':");
        Task washFloor = new Task("Помыть полы", "С новым средством");
        int washFloorId = taskManager.addTask(washFloor);
        System.out.println("Задача создана! Её ID: " + washFloorId);


        System.out.println("Смотрим задачу...");
        Task viewedTask = taskManager.getTaskByID(washFloorId);
        System.out.println("Посмотрели: " + viewedTask.getName());


        System.out.println("История просмотров сейчас:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(" - " + task.getName());
        }
        System.out.println();


        System.out.println("2. Обновляем задачу 'Помыть полы':");
        Task washFloorToUpdate = new Task(washFloor.getId(), "Не забыть помыть полы", "Можно и без средства",
                Status.IN_PROGRESS);
        boolean isUpdated = taskManager.updateTask(washFloorToUpdate);
        System.out.println("Задача обновлена: " + isUpdated);


        System.out.println("История после обновления (без просмотра):");
        for (Task task : taskManager.getHistory()) {
            System.out.println(" - " + task.getName());
        }
        System.out.println();


        System.out.println("3. Создаем эпик 'Сделать ремонт':");
        Epic flatRenovation = new Epic("Сделать ремонт", "Нужно успеть за отпуск");
        taskManager.addEpic(flatRenovation);
        System.out.println("Эпик создан! Его ID: " + flatRenovation.getId());


        System.out.println("Смотрим эпик...");
        Epic viewedEpic = taskManager.getEpicByID(flatRenovation.getId());
        System.out.println("Посмотрели: " + viewedEpic.getName());


        System.out.println("История просмотров сейчас:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(" - " + task.getName());
        }
        System.out.println();


        System.out.println("4. Добавляем подзадачи к эпику:");
        Subtask flatRenovationSubtask1 = new Subtask("Поклеить обои", "Обязательно светлые!",
                flatRenovation.getId());
        Subtask flatRenovationSubtask2 = new Subtask("Установить новую технику", "Старую продать на Авито",
                flatRenovation.getId());

        taskManager.addSubtask(flatRenovationSubtask1);
        taskManager.addSubtask(flatRenovationSubtask2);
        System.out.println("Добавлены подзадачи: 'Поклеить обои' и 'Установить новую технику'");


        System.out.println("Смотрим подзадачи...");
        Subtask viewedSubtask1 = taskManager.getSubtaskByID(flatRenovationSubtask1.getId());
        Subtask viewedSubtask2 = taskManager.getSubtaskByID(flatRenovationSubtask2.getId());
        System.out.println("Посмотрели: " + viewedSubtask1.getName() + " и " + viewedSubtask2.getName());


        System.out.println("История просмотров сейчас:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(" - " + task.getName());
        }
        System.out.println();


        System.out.println("5. Меняем статус подзадачи:");
        flatRenovationSubtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(flatRenovationSubtask2);
        System.out.println("Подзадача 'Установить новую технику' теперь выполнена!");


        System.out.println("Смотрим эпик еще раз...");
        Epic updatedEpic = taskManager.getEpicByID(flatRenovation.getId());
        System.out.println("Посмотрели эпик снова");


        System.out.println("История просмотров сейчас:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(" - " + task.getName());
        }
        System.out.println();


        System.out.println("6. ВСЕ ЗАДАЧИ В СИСТЕМЕ:");
        System.out.println("Простые задачи:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(" - " + task.getName() + " (статус: " + task.getStatus() + ")");
        }

        System.out.println("Эпики:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(" - " + epic.getName() + " (статус: " + epic.getStatus() + ")");

            System.out.println("   Подзадачи этого эпика:");
            for (Subtask subtask : taskManager.getEpicSubtasks(epic.getId())) {
                System.out.println("   * " + subtask.getName() + " (статус: " + subtask.getStatus() + ")");
            }
        }

        System.out.println("Все подзадачи:");
        for (Subtask subtask : taskManager.getSubtasks()) {
            System.out.println(" - " + subtask.getName() + " (эпик: " + subtask.getEpicID() + ")");
        }

        System.out.println("\nФИНАЛЬНАЯ ИСТОРИЯ ПРОСМОТРОВ:");
        System.out.println("Всего просмотрено: " + taskManager.getHistory().size() + " задач");
        for (Task task : taskManager.getHistory()) {
            System.out.println(" - " + task.getName() + " (" + task.getClass().getSimpleName() + ")");
        }

    }
}
