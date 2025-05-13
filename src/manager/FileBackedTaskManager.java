package manager;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    public static final String HEADER_STRING = "id,type,name,status,description,duration,startTime,epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Task createTask(Task task) {
        Task savedTask = super.createTask(task);
        save();
        return savedTask;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask savedSubtask = super.createSubtask(subtask);
        save();
        return savedSubtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic savedEpic = super.createEpic(epic);
        save();
        return savedEpic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    private void save() {
        try {
            List<Task> tasks = getAllTasks();
            List<Subtask> subtasks = getAllSubtasks();
            List<Epic> epics = getAllEpics();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
                writer.write(HEADER_STRING + System.lineSeparator());
                for (Task task : tasks) {
                    writer.write(toString(task) + System.lineSeparator());
                }
                for (Subtask subtask : subtasks) {
                    writer.write(toString(subtask) + System.lineSeparator());
                }
                for (Epic epic : epics) {
                    writer.write(toString(epic) + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при сохранении в файл.");
        }
    }

    private String toString(Task task) {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                getMinutesFromDuration(task),
                getStartTime(task),
                getEpicId(task));
    }

    private String getEpicId(Task task) {
        if (TaskType.SUBTASK.equals(task.getType())) {
            return ((Subtask) task).getEpicId() + "";
        }
        return "";
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ManagerLoadException("Произошла ошибка при загрузке из файла.");
        }

        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        lines.forEach(line -> {
            if (!line.startsWith(HEADER_STRING)) {
                Task task = manager.fromString(line);
                if (TaskType.SUBTASK.equals(task.getType())) {
                    manager.subtasks.put(task.getId(), (Subtask) task);
                } else if (TaskType.EPIC.equals(task.getType())) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else {
                    manager.tasks.put(task.getId(), task);
                }
            }
        });

        return manager;
    }

    private Task fromString(String value) {
        String[] values = value.split(",");
        if (values.length != 7 && values.length != 8) {
            throw new ManagerLoadException("Неверный формат записи.");
        }

        int id = Integer.parseInt(values[0]);
        TaskType type = TaskType.valueOf(values[1]);
        String name = values[2];
        Status status = Status.valueOf(values[3]);
        String description = values[4];
        Duration duration = restoreDuration(values[5]);
        LocalDateTime startTime = restoreStartTime(values[6]);
        int epicId = values.length == 8 ? Integer.parseInt(values[7]) : -1;

        switch (type) {
            case TASK:
                return restoreTask(id, name, description, status, duration, startTime);
            case SUBTASK:
                return restoreSubtask(id, name, description, status, epicId, duration, startTime);
            case EPIC:
                return restoreEpic(id, name, description, status, duration, startTime);
            default:
                throw new ManagerLoadException("Неизвестный тип задачи: " + type);
        }
    }

    private Task restoreTask(int id, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        Task task = new Task(name, description, status, duration, startTime);
        task.setId(id);
        return task;
    }

    private Subtask restoreSubtask(int id, String name, String description, Status status, int epicId, Duration duration, LocalDateTime  startTime) {
        Subtask subtask = new Subtask(name, description, status, epicId, duration, startTime);
        subtask.setId(id);
        return subtask;
    }

    private Epic restoreEpic(int id, String name, String description, Status status, Duration duration, LocalDateTime  startTime) {
        Epic epic = new Epic(name, description, duration, startTime);
        epic.setId(id);
        epic.setStatus(status);
        return epic;
    }

    private Long getMinutesFromDuration(Task task) {
        if (task.getDuration() == null) return null;
        return task.getDuration().toMinutes();
    }

    private Duration restoreDuration(String durationInMinutes) {
        if (durationInMinutes == null) return null;
        return Duration.ofMinutes(Long.parseLong(durationInMinutes));
    }

    private String getStartTime(Task task) {
        if (task.getStartTime() == null) return null;
        return task.getStartTime().toString();
    }

    private LocalDateTime restoreStartTime(String startTime) {
        if (startTime == null) return null;
        return LocalDateTime.parse(startTime);
    }

}
