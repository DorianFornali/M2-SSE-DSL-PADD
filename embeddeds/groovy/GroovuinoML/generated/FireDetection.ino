Transition finalized with result: OK
Transition finalized with result: OK
// Wiring code generated from an ArduinoML model
// Application name: Fire Detection

long debounce = 200;

enum STATE {idle, alarming};
STATE currentState = idle;

// constants
const float AUTO_CONSTANT_147 = 50.7;

boolean temperatureBounceGuard = false;
long temperatureLastDebounceTime = 0;

void setup(){
  pinMode(1, INPUT);  // temperature [Sensor]
  pinMode(11, OUTPUT); // buzzer [Actuator]
}

void loop() {
	switch(currentState){
		case idle:
			digitalWrite(11,LOW);
			temperatureBounceGuard = millis() - temperatureLastDebounceTime > debounce;
			if( temperatureBounceGuard && analogRead(1) > AUTO_CONSTANT_147 ) {
				temperatureLastDebounceTime = millis();
				currentState = alarming;
			}
		break;
		case alarming:
			digitalWrite(11,HIGH);
			temperatureBounceGuard = millis() - temperatureLastDebounceTime > debounce;
			if( temperatureBounceGuard && analogRead(1) <= AUTO_CONSTANT_147 ) {
				temperatureLastDebounceTime = millis();
				currentState = idle;
			}
		break;
	}
}
