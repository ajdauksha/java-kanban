import server.HttpTaskServer;
import utils.Managers;

public class ServerLauncher {

    public static void main(String[] args) {
        new HttpTaskServer(Managers.getDefaultFileBacked()).start();
    }

}
