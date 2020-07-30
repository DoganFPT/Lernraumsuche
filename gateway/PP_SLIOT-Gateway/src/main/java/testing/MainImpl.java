package testing;

import java.io.IOException;
import java.util.HashMap;

public class MainImpl {

    private static ServerHandler serverThread;
    private static ArgonHandler argonThread;
    private static HashMap<String, SensorData> data = new HashMap<>();


    public static void main (String[] args) throws Exception{
        dealWithServer();
        dealWithArgon();
        while(true){
            Thread.sleep(60000);
            data = argonThread.getCurrentData();
            serverThread.sendData(data);
        }
    }

    private static void dealWithArgon() throws IOException{
        argonThread = new ArgonHandler();
        argonThread.run();
    }

    private static void dealWithServer() throws IOException{
        serverThread = new ServerHandler();
        serverThread.run();
        serverThread.sendData("LE102", 25, 70, 20);
    }
}