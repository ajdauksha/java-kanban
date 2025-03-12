public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = manager.createTask(new Task(manager.getNextId(), "Задача 1", "Описание задачи 1", Status.NEW));
        Task task2 = manager.createTask(new Task(manager.getNextId(), "Задача 2", "Описание задачи 2", Status.NEW));
        System.out.println("Созданные задачи:");
        System.out.println(manager.getAllTasks());

        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);
        System.out.println("Задача с обновленным статусом:");
        System.out.println(manager.getTaskById(task1.getId()));

        manager.deleteTaskById(task1.getId());
        System.out.println("Список задач после удаления:");
        System.out.println(manager.getAllTasks());
    }
}
