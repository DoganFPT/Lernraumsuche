SYSTEM_THREAD(ENABLED);

void setup() {
    Serial.begin(9600);
    Mesh.subscribe("Sensor",myHandler);
}

void loop() {

}

void myHandler(const char *event, const char *data){
    Serial.println(data);
}