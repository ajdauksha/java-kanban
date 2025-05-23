package server;

import com.sun.net.httpserver.HttpServer;
import handlers.HistoryHttpHandler;
import handlers.PrioritizedHttpHandler;
import handlers.TasksHttpHandler;
import manager.TaskManager;
import manager.TaskType;
import utils.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;

    private HttpServer server;
    private final TaskManager manager;

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
    }

    public void start() {
        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(PORT), 0);
            server.createContext("/tasks", new TasksHttpHandler(manager, TaskType.TASK));
            server.createContext("/subtasks", new TasksHttpHandler(manager, TaskType.SUBTASK));
            server.createContext("/epics", new TasksHttpHandler(manager, TaskType.EPIC));
            server.createContext("/history", new HistoryHttpHandler(manager));
            server.createContext("/prioritized", new PrioritizedHttpHandler(manager));
            server.start();
            System.out.println("Сервер запущен на порте " + PORT);
        } catch (IOException e) {
            System.out.println("Произошла ошибка при запуске сервера. Завершение программы.");
            System.exit(1);
        }

    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен");
    }

}
