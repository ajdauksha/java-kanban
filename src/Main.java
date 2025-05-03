import manager.FileBackedTaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        FileBackedTaskManager manager = new FileBackedTaskManager(new File("tasks.csv"));

        System.out.println("Создаем задачи и эпики:");
        Task task1 = manager.createTask(new Task("Задача 1", "Описание задачи 1", Status.NEW));
        Task task2 = manager.createTask(new Task("Задача 2", "Описание задачи 2", Status.NEW));

        Epic epicWithSubtasks = manager.createEpic(new Epic("Эпик с подзадачами", "Описание эпика 1"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Подзадача 1", "Описание 1", Status.NEW, epicWithSubtasks.getId()));
        Subtask subtask2 = manager.createSubtask(new Subtask("Подзадача 2", "Описание 2", Status.NEW, epicWithSubtasks.getId()));
        Subtask subtask3 = manager.createSubtask(new Subtask("Подзадача 3", "Описание 3", Status.NEW, epicWithSubtasks.getId()));

        Epic epicWithoutSubtasks = manager.createEpic(new Epic("Эпик без подзадач", "Описание эпика 2"));

        System.out.println("Все задачи:");
        System.out.println(manager.getAllTasks());
        System.out.println("Все эпики:");
        System.out.println(manager.getAllEpics());
        System.out.println("Все подзадачи:");
        System.out.println(manager.getAllSubtasks());

        System.out.println("Запрашиваем задачи в разном порядке:");
        System.out.println("Запрашиваем task1, epicWithSubtasks, subtask1");
        manager.getTaskById(task1.getId());
        manager.getEpicById(epicWithSubtasks.getId());
        manager.getSubtaskById(subtask1.getId());
        System.out.println("История: " + manager.getHistory());

        System.out.println("Запрашиваем subtask1, task2, epicWithSubtasks, task1");
        manager.getSubtaskById(subtask1.getId());
        manager.getTaskById(task2.getId());
        manager.getEpicById(epicWithSubtasks.getId());
        manager.getTaskById(task1.getId());
        System.out.println("История: " + manager.getHistory());

        manager.getEpicById(epicWithoutSubtasks.getId());
        manager.getSubtaskById(subtask2.getId());
        manager.getSubtaskById(subtask3.getId());
        System.out.println("История после третьего запроса: " + manager.getHistory());

        System.out.println("Удаляем задачу, которая есть в истории (task1):");
        manager.deleteTaskById(task1.getId());
        System.out.println("История: " + manager.getHistory());

        System.out.println("Удаляем эпик с тремя подзадачами:");
        manager.deleteEpicById(epicWithSubtasks.getId());
        System.out.println("История: " + manager.getHistory());

        System.out.println("Финальное состояние истории:");
        System.out.println(manager.getHistory());
    }
}