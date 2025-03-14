import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = manager.createTask(new Task("Задача 1", "Описание задачи 1", Status.NEW));
        Task task2 = manager.createTask(new Task( "Задача 2", "Описание задачи 2", Status.NEW));
        System.out.println("Созданные задачи:");
        System.out.println(manager.getAllTasks());

        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);
        System.out.println("Задача с обновленным статусом:");
        System.out.println(manager.getTaskById(task1.getId()));

        manager.deleteTaskById(task1.getId());
        System.out.println("Список задач после удаления первой задачи:");
        System.out.println(manager.getAllTasks());

        manager.deleteAllTasks();
        System.out.println("Список задач после удаления всех задач:");
        System.out.println(manager.getAllTasks());

        Epic epic1 = manager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Epic epic2 = manager.createEpic(new Epic("Эпик 2", "Описание эпика 2"));
        System.out.println("Созданные эпики:");
        System.out.println(manager.getAllEpics());

        Subtask subtask1 = manager.createSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1.getId()));
        Subtask subtask2 = manager.createSubtask(new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW,  epic2.getId()));
        Subtask subtask3 = manager.createSubtask(new Subtask("Подзадача 3", "Описание подзадачи 3", Status.NEW,  epic1.getId()));
        System.out.println("Созданные подзадачи:");
        System.out.println(manager.getAllSubtasks());

        System.out.println("Эпики с подзадачами:");
        System.out.println(manager.getAllEpics());

        System.out.println("Меняем статус подзадачи на IN_PROGRESS (эпик долен стать IN_PROGRESS):");
        subtask2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask2);
        System.out.println(manager.getEpicById(epic2.getId()));

        System.out.println("Меняем статус подзадачи DONE (эпик долен стать DONE):");
        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask2);
        System.out.println(manager.getEpicById(epic2.getId()));

        System.out.println("Удаляем эпик 1':");
        manager.deleteEpicById(epic1.getId());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());
    }
}
