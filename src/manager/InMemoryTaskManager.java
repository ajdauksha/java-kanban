package manager;

import exceptions.ManagerOverlapException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import utils.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private static int nextId = 0;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private final TreeSet<Task> sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public Task createTask(Task task) {
        if (sortedTasks.stream().anyMatch(existingTask -> isOverlap(existingTask, task))) {
            throw new ManagerOverlapException("Невозможно создать задачу, так как она пересекается с другой задачей.");
        }

        final int id = ++nextId;
        task.setId(id);
        tasks.put(id, task);
        if (task.getStartTime() != null) {
            sortedTasks.add(task);
        }
        return task;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        cleanTasksHistory();
        sortedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    private void cleanTasksHistory() {
        tasks.values().forEach(task -> historyManager.remove(task.getId()));
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        return Optional.ofNullable(tasks.get(id))
                .map(task -> {
                    historyManager.add(task);
                    return task;
                });
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            if (sortedTasks.stream().anyMatch(existingTask -> isOverlap(existingTask, task))) {
                throw new ManagerOverlapException("Невозможно обновить задачу, так как она будет пересекаться с другой задачей.");
            }
            tasks.put(task.getId(), task);
            if (task.getStartTime() != null) {
                sortedTasks.remove(task);
                sortedTasks.add(task);
            }

        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.get(id).getStartTime() != null) {
            sortedTasks.remove(tasks.get(id));
        }
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (sortedTasks.stream().anyMatch(existingTask -> isOverlap(existingTask, subtask))) {
            throw new ManagerOverlapException("Невозможно создать подзадачу, так как она пересекается с другой задачей.");
        }
        final int id = ++nextId;
        subtask.setId(id);
        subtasks.put(id, subtask);
        sortedTasks.add(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic.getId());
        updateEpicEstimates(epic.getId());
        return subtask;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        cleanSubtasksHistory();
        sortedTasks.removeAll(subtasks.values());
        subtasks.clear();

        epics.values()
                .forEach(epic -> {
                    epic.getSubtaskIds().clear();
                    updateEpicStatus(epic.getId());
                    updateEpicEstimates(epic.getId());
                });
    }

    private void cleanSubtasksHistory() {
        subtasks.values().stream()
                .map(Subtask::getId)
                .forEach(historyManager::remove);
    }

    @Override
    public Optional<Subtask> getSubtaskById(int id) {
        return Optional.ofNullable(subtasks.get(id))
                .map(subtask -> {
                    historyManager.add(subtask);
                    return subtask;
                });
    }


    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            if (sortedTasks.stream().anyMatch(existingTask -> isOverlap(existingTask, subtask))) {
                throw new ManagerOverlapException("Невозможно обновить подзадачу, так как она будет пересекаеться с другой задачей.");
            }
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
            updateEpicEstimates(subtask.getId());
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        historyManager.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic.getId());
                updateEpicEstimates(epic.getId());
            }
        }
    }

    @Override
    public Epic createEpic(Epic epic) {
        final int id = ++nextId;
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        cleanEpicsHistory();
        cleanSubtasksHistory();
        epics.clear();
        subtasks.clear();
    }

    private void cleanEpicsHistory() {
        epics.values().stream()
                .map(Epic::getId)
                .forEach(historyManager::remove);
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        return Optional.ofNullable(epics.get(id))
                .map(epic -> {
                    historyManager.add(epic);
                    return epic;
                });
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic != null) {
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        historyManager.remove(id);
        if (epic != null) {
            epic.getSubtaskIds().forEach(historyManager::remove);
        }
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Subtask> subtasks = getSubtasksByEpicId(epicId);
        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean isAllNewTasks = true;
        boolean isAllDoneTasks = true;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != Status.NEW) isAllNewTasks = false;
            if (subtask.getStatus() != Status.DONE) isAllDoneTasks = false;
        }

        if (isAllNewTasks) {
            epic.setStatus(Status.NEW);
        } else if (isAllDoneTasks) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void updateEpicEstimates(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Subtask> subtasks = getSubtasksByEpicId(epicId);
        if (subtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
            return;
        }

        List<Task> sortedSubtasks = subtasks.stream()
                .sorted(Comparator.comparing(Task::getStartTime))
                .collect(Collectors.toList());

        LocalDateTime startTime = sortedSubtasks.get(0).getStartTime();
        LocalDateTime endTime = sortedSubtasks.get(sortedSubtasks.size() - 1).getEndTime();
        Duration totalDuration = Duration.between(startTime, endTime);

        epic.setStartTime(sortedSubtasks.get(0).getStartTime());
        epic.setEndTime(endTime);
        epic.setDuration(totalDuration);
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return new TreeSet<>(sortedTasks);
    }

    private boolean isOverlap(Task task1, Task task2) {
        return task1.getStartTime().isBefore(task2.getEndTime())
                && task1.getEndTime().isAfter(task2.getStartTime());
    }

}
