package testing;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class RoomTesting {

    private static File roomFile = new File("/home/" + System.getProperty("user.name") + "/Documents/sliot/roomNames.json");
    private static String content = "";

    public static void main(String[] args){
        try (BufferedReader br = new BufferedReader(new FileReader(roomFile))){
            String line;
            while((line = br.readLine()) != null) {
                content += line;
            }
            System.out.println(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonRooms = new JSONObject(content);
        JSONArray jsonRoomArray = jsonRooms.getJSONArray("rooms");
        for(int i = 0; i < jsonRoomArray.length(); i++){
            System.out.println(jsonRoomArray.get(i).toString());
        }
    }
}
