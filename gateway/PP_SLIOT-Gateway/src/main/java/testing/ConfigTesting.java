package testing;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigTesting {

    private static final String configFile = "SLIOT_CONFIG.cfg";
    private static final Properties projectProperties = new Properties();

    public static void main(String[] args) {
        setUpConfig();
    }

    private static void setUpConfig() {
        try (FileInputStream fis = new FileInputStream(configFile)) {
            projectProperties.load(fis);
            System.out.println(projectProperties.get("ROOM_FILE") + ", " + projectProperties.get("FALSE_CODE") + ", " + projectProperties.get("SERVER_URL") + ", " + projectProperties.get("SENDING_INTERVAL"));
            if(projectProperties.get("SENDING_INTERVAL").equals("")) {
                System.out.println(projectProperties.get("SENDING_INTERVAL") + "empty");
            } else {
                System.out.println(projectProperties.get("SENDING_INTERVAL") + "data");
            }
        } catch (IOException e) {
            System.err.println("ERROR (Argon) » Reading config-file: Something went wrong!");
        }
    }
}
