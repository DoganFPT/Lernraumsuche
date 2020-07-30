package project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class MainImpl {

    private static ServerHandler serverThread;
    private static ArgonHandler argonThread;
    private static HashMap<String, SensorData> data = new HashMap<>();
    private static final String configFile = "SLIOT_CONFIG.cfg";
    private static final long sendingIntervalDefault = 15000;
    private static final String roomPathDefault = "/home/" + System.getProperty("user.name") + "/Documents/sliot/rooms.json";
    private static String serverURL;
    private static long sendingInterval;
    private static final Properties projectProperties = new Properties();

    public static void main (String[] args) throws Exception{
        // setting up config w/ config file
        setUpConfig();

        // starting server thread and executing server related code
        dealWithServer();
        // starting argon thread and executing Argon related code
        dealWithArgon();

        // sending data to server every <sendingInterval> ms
        while(true) {
            Thread.sleep(sendingInterval);
            data = argonThread.getCurrentData();
            serverThread.sendData(data);
        }
    }

    private static void setUpConfig() {
        try (FileInputStream fis = new FileInputStream(configFile)) {
            projectProperties.load(fis);

            if(projectProperties.get("SENDING_INTERVAL").equals("")) {
                sendingInterval = sendingIntervalDefault;
            } else {
                sendingInterval = Long.parseLong(projectProperties.get("SENDING_INTERVAL").toString());
            }
            serverURL = projectProperties.get("SERVER_URL").toString();
        } catch (IOException e) {
            System.err.println("ERROR (Argon) » Reading config-file: Something went wrong!");
        }
    }

    private static void dealWithArgon() {
        argonThread = new ArgonHandler();
        String roomFilePath = roomPathDefault;
        // loading configurations
        if(!projectProperties.get("ROOM_FILE").equals("")) {
            roomFilePath = projectProperties.get("ROOM_FILE").toString();
        }
        argonThread.configure(roomFilePath, projectProperties.get("FALSE_CODE").toString());
        argonThread.start();
    }

    private static void dealWithServer() {
        serverThread = new ServerHandler(serverURL);
        serverThread.start();
    }
}