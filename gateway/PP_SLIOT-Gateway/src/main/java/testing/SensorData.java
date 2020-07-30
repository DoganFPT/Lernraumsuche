package testing;

public class SensorData {

    private String room;
    private float temp, hum, noise;

    public SensorData(String tRoom, float tTemp, float tHum, float tNoise){
        this.room = tRoom;
        this.temp = tTemp;
        this.hum = tHum;
        this.noise = tNoise;
    }

    public SensorData(){
        this.room = "-1";
        this.temp = -1f;
        this.hum = -1f;
        this.noise = -1f;
    }

    public String getRoom() {
        return room;
    }

    public float getTemp() {
        return temp;
    }

    public float getHum() {
        return hum;
    }

    public float getNoise() {
        return noise;
    }
}
