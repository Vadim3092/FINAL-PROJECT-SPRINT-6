package ru.practicum;

import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task washFloor = new Task("Помыть полы", "С новым средством");
        int washFloorCreated = taskManager.addTask(washFloor);
        System.out.println(washFloorCreated);

        Task washFloorToUpdate = new Task(washFloor.getId(), "Не забыть помыть полы", "Можно и без средства",
                Status.IN_PROGRESS);
        boolean washFloorUpdated = taskManager.updateTask(washFloorToUpdate);
        System.out.println(washFloorUpdated);


        Epic flatRenovation = new Epic("Сделать ремонт", "Нужно успеть за отпуск");
        taskManager.addEpic(flatRenovation);
        System.out.println(flatRenovation);
        Subtask flatRenovationSubtask1 = new Subtask("Поклеить обои", "Обязательно светлые!",
                flatRenovation.getId());
        Subtask flatRenovationSubtask2 = new Subtask("Установить новую технику", "Старую продать на Авито",
                flatRenovation.getId());
        taskManager.addSubtask(flatRenovationSubtask1);
        taskManager.addSubtask(flatRenovationSubtask2);
        System.out.println(flatRenovation);
        flatRenovationSubtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(flatRenovationSubtask2);
        System.out.println(flatRenovation);
    }
}
