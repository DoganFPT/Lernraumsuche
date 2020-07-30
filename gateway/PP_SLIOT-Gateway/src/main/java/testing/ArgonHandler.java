package testing;

import com.fazecast.jSerialComm.SerialPort;

import java.io.InputStream;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ArgonHandler implements Runnable {

    private static HashMap<String, SensorData> currentData = new HashMap<>(); //Hashmap for storing Input Data

    @Override
    public void run() {
        try {
            serialStream(getSerialComms());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Finding SerialPort
    public static SerialPort getSerialComms(){
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        try{
            if(serialPorts!=null) {
                for (SerialPort sp : serialPorts) {
                    System.out.println("DEBUG (Argon) » Descriptive Port Name: " + sp.getDescriptivePortName() + ",\nSystem Port Name: " + sp.getSystemPortName() + ",\nPort Description" + sp.getPortDescription());
                    return serialPorts[0];          //serialPorts[0], cuz only 1 USB connected (Argon)
                }
            } else {
                return null;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // Reading Argon input w/ Inputstream(Scanner)
    public static void serialStream(SerialPort sp){
        sp.openPort();
        System.out.println("DEBUG (Argon) » Port is open: " + sp.isOpen());
        sp.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        int msgCounter = 1;
        String data;
        InputStream in = sp.getInputStream();
        Scanner scanner = new Scanner(in);
        try {
            while (true) {
                data = scanner.nextLine();
                addToHash(toSensorData(data));
                System.out.println("DEBUG (Argon) » Message#: " + msgCounter + " New Line: " + data);
                msgCounter++;
            }
        } catch (InputMismatchException e) {
            System.err.println("ERROR (Argon) » Retrieved token does not match expected pattern or type!");
        }
        sp.closePort();
    }

    // Formatting Input-String to match SensorData structure
    private static SensorData toSensorData(String data){
        String[] splitter = data.split(",");
        return new SensorData(splitter[0], Float.parseFloat(splitter[1]), Float.parseFloat(splitter[2]), Float.parseFloat(splitter[3]));
    }

    // Returning HashMap currentData
    public HashMap<String, SensorData> getCurrentData(){
        return currentData;
    }

    // Adding new Data to HashMap
    private static void addToHash(SensorData data){
        if(!currentData.containsKey(data.getRoom())){
            currentData.put(data.getRoom(), data);
        } else {
            currentData.replace(data.getRoom(), data);
        }
    }
}
