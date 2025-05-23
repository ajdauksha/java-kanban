package handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerOverlapException;
import exceptions.NotFoundException;
import manager.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHttpHandler extends BaseHttpHandler {

    public PrioritizedHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        List<String> pathParts = getPathParts(path);
        String method = exchange.getRequestMethod();

        try {
            if (method.equals("GET")) {
                handleGet(exchange, pathParts);
            } else {
                sendNotImplemented(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Id должен быть целым числом");
        } catch (ManagerOverlapException e) {
            sendHasInteractions(exchange, "Задача имеет пересечения по времени выполнения с уже существующими");
        } catch (IOException e) {
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, List<String> pathParts) throws IOException {
        if (pathParts.size() != 1) {
            sendNotImplemented(exchange);
            return;
        }

        sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()));
    }
}
