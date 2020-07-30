package project;

import com.fazecast.jSerialComm.SerialPort;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ArgonHandler extends Thread {

    /**
     * Parameter names + values:
     * room    : String
     * temp    : float
     * hum     : float
     * noise   : float
     * wifi    : float
     */
    private static float falseCode = 666f;
    private static final String timeRequestCode = "Time Request";
    private static SerialPort sp;
    private static JSONArray roomArray;
    private static HashMap<String, SensorData> currentData = new HashMap<>(); //Hashmap for storing Input Data

    @Override
    public void run() {
        try {
            sp = getSerialComms();
            sendTime();
            serialStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // setup of configurations from .cfg-File
    public static void configure(String pathToRoomFile, String pFalseCodeData) {
        if(pathToRoomFile.equals("")) {
            prepareRooms(new File("/home/" + System.getProperty("user.name") + "/Documents/sliot/rooms.json"));
        } else {
            prepareRooms(new File(pathToRoomFile));
        }
        falseCode = Float.parseFloat(pFalseCodeData);
    }

    // Finding SerialPort
    public static SerialPort getSerialComms() {
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        try {
            if (serialPorts != null) {
                for (SerialPort sp : serialPorts) {
                    System.out.println("DEBUG (Argon) » Descriptive Port Name: " + sp.getDescriptivePortName() + ", System Port Name: " + sp.getSystemPortName()
                            + ", Port Description: " + sp.getPortDescription());
                    return serialPorts[0];          //only 1 USB connected (Argon) -> serialPorts[0]
                }
            } else {
                System.err.println("ERROR (Argon) » No Port detected.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // "Publish" time to Argon to be used by sensor-argons
    public static void sendTime(){
        try {
            Thread.sleep(1000);
            sp.openPort();
            System.out.println("DEBUG (Argon) » Sending time to Argon");
            String currentTime = System.currentTimeMillis() + "";
            OutputStream os = sp.getOutputStream();
            os.write(currentTime.getBytes());
            os.flush();
        } catch (IOException e) {
            System.err.println("ERROR (Argon) » Something went wrong whilst transmitting time! (I/O)");
        } catch (InterruptedException e) {
            System.err.println("ERROR (Argon) » Something went wrong whilst transmitting time! (Thread)");
        }
    }

    // Reading Argon input w/ Inputstream(Scanner)
    public static void serialStream() {
        sp.openPort();
        System.out.println("DEBUG (Argon) » Port is open (read): " + sp.isOpen());
        sp.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        int msgCounter = 1;
        String data;
        InputStream in = sp.getInputStream();
        Scanner scanner = new Scanner(in);
        try {
            while (true) {
                data = scanner.nextLine();
                if(detectTimeRequest(data)) {
                    sendTime();
                } else {
                    validateForHash(toSensorData(data));
                }
                System.out.println("DEBUG (Argon) » Message#: " + msgCounter + " New Line: " + data);
                msgCounter++;
            }
        } catch (InputMismatchException e) {
            System.err.println("ERROR (Argon) » Retrieved token does not match expected pattern or type!");
        } catch (NoSuchElementException e) {

        }
        sp.closePort();
    }

    // Detecting if time was requested
    private static boolean detectTimeRequest(String data) {
        if(data.toLowerCase().equals(timeRequestCode.toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }

    // Formatting Input-String to match SensorData structure
    private static SensorData toSensorData(String data) {
        String[] splitter = data.split(",");
        return new SensorData(checkRoom(splitter[0]), checkTemp(Float.parseFloat(splitter[1])), checkHum(Float.parseFloat(splitter[2])),
                checkNoise(Float.parseFloat(splitter[3])), checkWifi(Float.parseFloat(splitter[4])));
    }

    // Returning HashMap currentData
    public HashMap<String, SensorData> getCurrentData() {
        return currentData;
    }

    /**
     *  Data validation & -integrity
     */

    public static void prepareRooms(File roomFile) {
        String content = "";
        try (BufferedReader br = new BufferedReader(new FileReader(roomFile))){
            String line;
            while((line = br.readLine()) != null) {
                content += line;
            }
        } catch (Exception e) {
            System.err.println("ERROR (Argon) » Loading room file: Something went wrong!");
        }
        JSONObject jsonRooms = new JSONObject(content);
        roomArray = jsonRooms.getJSONArray("rooms");
        System.out.println("DEBUG (Argon) » Loading room file: Successful!");
    }

    // Checking for valid room
    private static String checkRoom(String preRoom) {
        String resRoom = String.valueOf(falseCode);
        for(int i = 0; i < roomArray.length(); i++){
            if(preRoom.toUpperCase().equals(roomArray.get(i).toString().toUpperCase())) {
                resRoom = roomArray.get(i).toString();
                break;
            }
        }
        return resRoom;
    }

    // Checking temperature for false data
    private static float checkTemp(float preTemp) {
        if((-10f <= preTemp) && (preTemp <= 45f))
            return preTemp;
        else
            return falseCode;
    }

    // Checking humidity for false data
    private static float checkHum(float preHum) {
        if((0 <= preHum) && (preHum <= 100))
            return preHum;
        else
            return falseCode;
    }

    // Checking noise for false data
    private static float checkNoise(float preNoise) {
        if((0 <= preNoise) && (preNoise <= 2000))
            return convertNoise(preNoise);
        else
            return falseCode;
    }

    // Converting raw data
    /**
     * Formel-Quelle: https://blog.yavilevich.com/2016/08/arduino-sound-level-meter-and-spectrum-analyzer/
     */
    private static float convertNoise(float preNoise) {
        float noise = 20 * (float) (Math.log(preNoise) / Math.log(10));
        return noise;
    }

    // Checking wifi for false data
    private static float checkWifi(float preWifi) {
        if((-1 >= preWifi) && (preWifi >= -127))
            return convertWifi(preWifi);
        else
            return falseCode;
    }

    // Converting Wifi-Signal to Scale
    private static float convertWifi(float preWifi) {
        if(preWifi>(-30))
            return -30f;
        else if(preWifi<(-90))
            return -90f;
        else
            return preWifi;
    }

    // Checking for fault-data-entries
    private static void validateForHash(SensorData data) {
        if(!(data.getRoom().equals(falseCode) || data.getHum()==falseCode || data.getNoise()==falseCode || data.getTemp()==falseCode || data.getWifi()==falseCode))
            addToHash(data);
        else
            System.err.println("ERROR (Argon) » Data entry ("+data.getRoom()+","+data.getHum()+","+data.getNoise()+","+data.getTemp()+","+data.getWifi()+") " +
                    "contains at least one defective value.");

    }

    // Adding new Data to HashMap
    private static void addToHash(SensorData data) {
        if (!currentData.containsKey(data.getRoom())) {
            currentData.put(data.getRoom(), data);
        } else {
            currentData.replace(data.getRoom(), data);
        }
    }
}