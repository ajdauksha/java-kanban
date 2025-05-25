package handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerOverlapException;
import exceptions.NotFoundException;
import manager.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHttpHandler extends BaseHttpHandler {

    public HistoryHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    protected void handleGet(HttpExchange exchange, List<String> pathParts) throws IOException {
        if (pathParts.size() != 1) {
            sendNotImplemented(exchange);
            return;
        }

        sendText(exchange, gson.toJson(taskManager.getHistory()));
    }

}
