package manager;

import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (!isValidTask(task)) {
            return;
        }
        maintainHistorySize();
        history.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }

    private boolean isValidTask(Task task) {
        return task != null;
    }

    private void maintainHistorySize() {
        if (history.size() >= 10) {
            history.remove(0);
        }
    }

}
