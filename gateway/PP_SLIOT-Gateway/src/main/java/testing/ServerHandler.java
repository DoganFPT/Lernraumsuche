package testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class ServerHandler implements Runnable {

    /** Parameter names + values:
     *      room    : String
     *      temp    : float
     *      hum     : float
     *      noise   : float
     *
     * Server URL: https://.stud02.vs.uni-due.de/home/sliot/gateway/add_data_custom.php
     */

    private final String url = "http://stud02.vs.uni-duisburg-essen.de/gateway/add_data_custom.php";
    private HttpURLConnection serverCon;

    @Override
    public void run() { }

    // Sending data given by values
    public void sendData(String room, float temp, float hum, float noise) {

        // Setup get request parameters and GET target URL
        String params = "?+room=%27" + room + "%27&temp=" + temp + "&hum=" + hum + "&noise=" + noise;
        String getURL = url + params;
        System.out.println("DEBUG (Server) » getURL: " +getURL);

        try {
            URL serverURL = new URL(getURL);
            serverCon = (HttpURLConnection) serverURL.openConnection();
            serverCon.setRequestMethod("GET");
            int responseCode = serverCon.getResponseCode();
            System.out.println("DEBUG (Server) » GET Response Code: " + responseCode);
            // Connection success (responseCode = 200)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(serverCon.getInputStream()));
                String lineInput;
                StringBuffer serverResponse = new StringBuffer();
                // Server Response
                while ((lineInput = in.readLine()) != null) {
                    serverResponse.append(lineInput);
                }
                in.close();
                System.out.println("DEBUG (Server) » Server Response: " + serverResponse.toString());
            } else {
                System.err.println("ERROR (Server) » Something went wrong! GET request did not work.");
            }
        } catch (UnknownHostException e) {
            System.err.println("ERROR (Server) » Server is either down or IP doesn't exist.");
            e.printStackTrace();
        } catch (ConnectException e) {
            System.err.println("ERROR (Server) » Timeout.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sending data given by data HashMap
    public void sendData(HashMap<String, SensorData> data) {
        for(Map.Entry<String, SensorData> dataEntry : data.entrySet()) {
            // Setup get request parameters and GET target URL
            String params = "?+room=%27" + dataEntry.getValue().getRoom() + "%27&temp=" + dataEntry.getValue().getTemp() + "&hum=" + dataEntry.getValue().getHum() + "&noise=" + dataEntry.getValue().getNoise();
            String getURL = url + params;
            System.out.println("DEBUG (Server) » getURL: " + getURL);

            try {
                URL serverURL = new URL(getURL);
                serverCon = (HttpURLConnection) serverURL.openConnection();
                serverCon.setRequestMethod("GET");
                int responseCode = serverCon.getResponseCode();
                System.out.println("DEBUG (Server) » GET Response Code: " + responseCode);
                // Connection success (responseCode = 200)
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(serverCon.getInputStream()));
                    String lineInput;
                    StringBuffer serverResponse = new StringBuffer();
                    // Server Response
                    while ((lineInput = in.readLine()) != null) {
                        serverResponse.append(lineInput);
                    }
                    in.close();
                    System.out.println("DEBUG (Server) » Server Response: " + serverResponse.toString());
                } else {
                    System.err.println("ERROR (Server) » Something went wrong! GET request did not work.");
                }
            } catch (UnknownHostException e) {
                System.err.println("ERROR (Server) » Server is either down or IP doesn't exist.");
                e.printStackTrace();
            } catch (ConnectException e) {
                System.err.println("ERROR (Server) » Timeout.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
