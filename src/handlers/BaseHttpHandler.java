package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ManagerOverlapException;
import exceptions.NotFoundException;
import manager.TaskManager;
import utils.JsonHelper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseHttpHandler implements HttpHandler {

    protected final TaskManager taskManager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = JsonHelper.getCustomisedGson();
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


    protected void handleGet(HttpExchange exchange, List<String> pathParts) throws IOException {
        sendNotImplemented(exchange);
    }

    protected void handlePost(HttpExchange exchange, List<String> pathParts) throws IOException {
        sendNotImplemented(exchange);
    }

    protected void handleDelete(HttpExchange exchange, List<String> pathParts) throws IOException {
        sendNotImplemented(exchange);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendOkWithText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(201, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        h.sendResponseHeaders(404, 0);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h, String message) throws IOException {
        h.sendResponseHeaders(406, 0);
        byte[] resp = gson.toJson(Map.of("error", message)).getBytes(StandardCharsets.UTF_8);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendBadRequest(HttpExchange h, String message) throws IOException {
        h.sendResponseHeaders(400, 0);
        byte[] resp = gson.toJson(Map.of("error", message)).getBytes(StandardCharsets.UTF_8);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotImplemented(HttpExchange h) throws IOException {
        h.sendResponseHeaders(501, 0);
        h.close();
    }

    protected void sendInternalError(HttpExchange h) throws IOException {
        h.sendResponseHeaders(500, 0);
        h.close();
    }

    protected List<String> getPathParts(String path) {
        return Arrays.stream(path.split("/"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

}
