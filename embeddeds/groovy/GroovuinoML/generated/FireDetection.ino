Creating analog sensor with name: temperature
Creating digital actuator with name: buzzer
Creating digital actuator with pin: 11
Transition finalized with result: OK
Transition finalized with result: OK
// Wiring code generated from an ArduinoML model
// Application name: Fire Detection

long debounce = 200;

enum STATE {idle, alarming};
STATE currentState = idle;

// constants
const float AUTO_CONSTANT_758 = 50.7;

boolean temperatureBounceGuard = false;
long temperatureLastDebounceTime = 0;

void setup(){
  pinMode(1, INPUT);  // temperature [Analog Sensor] CONNECT TO A1
  pinMode(11, OUTPUT); // buzzer [Digital Actuator] CONNECT TO D11
}

void loop() {
	switch(currentState){
		case idle:
			digitalWrite(11,LOW);
			temperatureBounceGuard = millis() - temperatureLastDebounceTime > debounce;
			if( temperatureBounceGuard && analogRead(1) > AUTO_CONSTANT_758 ) {
				temperatureLastDebounceTime = millis();
				currentState = alarming;
			}
		break;
		case alarming:
			digitalWrite(11,HIGH);
			temperatureBounceGuard = millis() - temperatureLastDebounceTime > debounce;
			if( temperatureBounceGuard && analogRead(1) <= AUTO_CONSTANT_758 ) {
				temperatureLastDebounceTime = millis();
				currentState = idle;
			}
		break;
	}
}
