/*================================*/
/*
PRAXISPROJEKT WS 19/20
PROJECT NAME: Smarte Lernraumsuche
Code description: Sensor Argon Code
Authors: Alexander Hochalter, Erik Jakobs, Reza Asif
Date last edited: 26.01.2020
*/
/*================================*/


#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BME280.h>
SYSTEM_MODE(MANUAL);
SYSTEM_THREAD(ENABLED);


/*================================*/
/*ARGON PIN SETUP AND CONSTANT DECLERATIONS*/
/*================================*/

static String wifiSSID = "eduroam";
static String roomID = "BC303";
static int sleepStartTime = 18;
static int sleepEndTime = 6;
static int loudnessSensorInputPin = A0;
static int loudnessSensorOutputPin = A1;
Adafruit_BME280 bme;

int tempRSSI;
int strongestRSSI;



/*================================*/
/*ARGON SETUP METHOD*/
/*================================*/
void setup() {

	Serial.begin(9600);
	bme.begin();
	pinMode(loudnessSensorOutputPin, OUTPUT);
	Mesh.on();
	Mesh.connect();
	waitUntil(Mesh.ready);
	Mesh.subscribe("GatewayTime", gatewayTimeHandler);
	Mesh.publish("TimeRequest");
	delay(2000);
	Mesh.off();

}





/*================================*/
/*MAIN PROGRAM-LOOP*/
/*================================*/

void loop() {

  	int wifiSignal = measureWiFiSignal();
  	int soundLevel = measureSoundLevel();
	float temperature = bme.readTemperature();
  	float humidity = bme.readHumidity();
  	sendSensorData(temperature, humidity, soundLevel, wifiSignal);
	sleep();

}







/*================================*/
/*SENSOR METHODS*/
/*================================*/

/*METHOD: SET CURRENT TIME. CURRENT TIME = TIME ON GATEWAY*/
void gatewayTimeHandler(const char *event, const char *data) {

	String gatewayTimeString = data;
	time_t gatewayTime = gatewayTimeString.toInt();
	Time.setTime(gatewayTime);

}

/*METHOD: TURN WIFI SENSOR ON AND SCAN. AFTER SCANNING
					TURN OFF TO SAVE POWER*/
int measureWiFiSignal() {

	tempRSSI = 0;
	WiFi.on();
	delay(1000);
	for (int i = 0; i < 5; i++) {
		strongestRSSI = -128;
		WiFi.scan(wifiAccessPointHandler);
	}
  	WiFi.off();
	int avgRSSI = tempRSSI / 5;
  	return avgRSSI;

}

/*METHOD: SCANS FOR STRONGEST WIFI SSID (= eduroam)*/
void wifiAccessPointHandler(WiFiAccessPoint* wap, void* data) {

    WiFiAccessPoint& ap = *wap;
    if(wifiSSID.equals(ap.ssid) && ap.rssi < 0 && ap.rssi > strongestRSSI) {
        strongestRSSI = ap.rssi;
		tempRSSI =+ ap.rssi;
    }

}



/*METHOD: GET SOUNDLEVEL FROM MIC*/
int measureSoundLevel() {

	int tempSoundLevel = 0;
  	analogWrite(loudnessSensorOutputPin, 255);
	delay(1000);
	for (int i = 0; i < 50; i++) {
		tempSoundLevel = tempSoundLevel + analogRead(loudnessSensorInputPin);
		delay(100);
	}
 	analogWrite(loudnessSensorOutputPin, 0);
	int avgSoundLevel = tempSoundLevel / 50;
 	return avgSoundLevel;

}

/*METHOD: PUBLISH SENSOR DATA AS STRING TO MESH (for debugging: print to serial)*/
void sendSensorData(int temperature, int humidity, int soundLevel, int wifiSignal) {

  	Mesh.on();
	Mesh.connect();
	waitUntil(Mesh.ready);
  	String sensorData = roomID + "," + String(temperature) + "," + String(humidity) + "," + String(soundLevel) + ","
	  					+  String(wifiSignal) + "," + String(Time.now());
  	Mesh.publish("Sensor", sensorData);
	Serial.println("Sensordaten: " + sensorData);
	Serial.println("Uhrzeit: " + String(Time.hour()) + ":" + String(Time.minute()));
	delay(2000);
  	Mesh.off();

}


void sleep() {

	boolean deepSleep = false;
	int sleepDuration = 0;
	if (Time.minute() < 15) {
		sleepDuration = (15 - Time.minute()) * 60;
	} else if (Time.minute() < 30) {
		sleepDuration = (30 - Time.minute()) * 60;
	} else if (Time.minute() < 45) {
		sleepDuration = (45 - Time.minute()) * 60;
	} else {
		sleepDuration = (60 - Time.minute()) * 60;
	}
	if (sleepStartTime == Time.hour()) {
		if (sleepStartTime > sleepEndTime) {
			sleepDuration = (24 - sleepStartTime + sleepEndTime) * 3600;
			deepSleep = true;
		} else {
			sleepDuration = (sleepEndTime - sleepStartTime) * 3600; 
			deepSleep = true;
		}
	}
	Serial.println("Sleep-Dauer: " + String(sleepDuration) + " Sekunden");
	System.sleep({}, {}, sleepDuration);
	if (deepSleep) {
		Mesh.on();
		Mesh.connect();
		waitUntil(Mesh.ready);
		Mesh.publish("TimeRequest");
		delay(2000);
  		Mesh.off();
	}

}
