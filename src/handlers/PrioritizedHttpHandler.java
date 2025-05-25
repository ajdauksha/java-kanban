package handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHttpHandler extends BaseHttpHandler {

    public PrioritizedHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, List<String> pathParts) throws IOException {
        if (pathParts.size() != 1) {
            sendNotImplemented(exchange);
            return;
        }

        sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()));
    }
}
