package handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerOverlapException;
import exceptions.NotFoundException;
import manager.TaskManager;
import manager.TaskType;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHttpHandler extends BaseHttpHandler {

    private final TaskType taskType;

    public TasksHttpHandler(TaskManager taskManager, TaskType taskType) {
        super(taskManager);
        this.taskType = taskType;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        List<String> pathParts = getPathParts(path);
        String method = exchange.getRequestMethod();

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange, pathParts);
                    break;
                case "POST":
                    handlePost(exchange, pathParts);
                    break;
                case "DELETE":
                    handleDelete(exchange, pathParts);
                    break;
                default:
                    sendNotImplemented(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Id должен быть целым числом");
        } catch (ManagerOverlapException e) {
            sendHasInteractions(exchange, "Задача имеет пересечения по времени выполнения с уже существующими");
        }

    }

    private void handleGet(HttpExchange exchange, List<String> pathParts) throws IOException {
        if (pathParts.size() > 2) {
            sendNotImplemented(exchange);
            return;
        }

        if (pathParts.size() == 1) {
            switch (taskType) {
                case TASK:
                    sendText(exchange, gson.toJson(taskManager.getAllTasks()));
                    break;
                case EPIC:
                    sendText(exchange, gson.toJson(taskManager.getAllEpics()));
                    break;
                case SUBTASK:
                    sendText(exchange, gson.toJson(taskManager.getAllSubtasks()));
                    break;
                default:
                    sendBadRequest(exchange, "Неизвестный тип задачи");
            }
        } else {

            String taskId = pathParts.get(1);

            switch (taskType) {
                case TASK:
                    sendText(exchange, gson.toJson(taskManager.getTaskById(Integer.parseInt(taskId)).get()));
                    break;
                case EPIC:
                    sendText(exchange, gson.toJson(taskManager.getEpicById(Integer.parseInt(taskId)).get()));
                    break;
                case SUBTASK:
                    sendText(exchange, gson.toJson(taskManager.getSubtaskById(Integer.parseInt(taskId)).get()));
                    break;
                default:
                    sendBadRequest(exchange, "Неизвестный тип задачи");
            }
        }
    }

    private void handlePost(HttpExchange exchange, List<String> pathParts) throws IOException {
        if (pathParts.size() > 1) {
            sendNotImplemented(exchange);
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        switch (taskType) {
            case TASK:
                Task task = gson.fromJson(body, Task.class);
                if (null == task.getId()) {
                    taskManager.createTask(task);
                } else {
                    taskManager.updateTask(task);
                }
                sendOkWithText(exchange, gson.toJson(task));
                break;
            case EPIC:
                Epic epic = gson.fromJson(body, Epic.class);
                if (null == epic.getId()) {
                    taskManager.createEpic(epic);
                } else {
                    taskManager.updateEpic(epic);
                }
                sendOkWithText(exchange, gson.toJson(epic));
                break;
            case SUBTASK:
                Subtask subtask = gson.fromJson(body, Subtask.class);
                if (null == subtask.getId()) {
                    taskManager.createSubtask(subtask);
                } else {
                    taskManager.updateSubtask(subtask);
                }
                sendOkWithText(exchange, gson.toJson(subtask));
                break;
            default:
                sendBadRequest(exchange, "Неизвестный тип задачи");
        }
    }

    private void handleDelete(HttpExchange exchange, List<String> pathParts) throws IOException {
        if (pathParts.size() != 2) {
            sendNotImplemented(exchange);
            return;
        }

        String taskId = pathParts.get(1);

        switch (taskType) {
            case TASK:
                taskManager.deleteTaskById(Integer.parseInt(taskId));
                sendText(exchange, "Задача успешно удалена");
                break;
            case EPIC:
                taskManager.deleteEpicById(Integer.parseInt(taskId));
                sendText(exchange, "Задача успешно удалена");
                break;
            case SUBTASK:
                taskManager.deleteSubtaskById(Integer.parseInt(taskId));
                sendText(exchange, "Задача успешно удалена");
                break;
            default:
                sendBadRequest(exchange, "Неизвестный тип задачи");
        }
    }

}
