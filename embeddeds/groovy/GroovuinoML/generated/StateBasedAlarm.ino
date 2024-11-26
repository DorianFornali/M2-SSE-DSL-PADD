// Wiring code generated from an ArduinoML model
// Application name: State Based Alarm

long debounce = 200;

enum STATE {on, off};
STATE currentState = off;

boolean buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

void setup(){
  pinMode(9, INPUT);  // button [Sensor]
  pinMode(12, OUTPUT); // led [Actuator]
}

void loop() {
	switch(currentState){
		case on:
			digitalWrite(12,HIGH);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if( buttonBounceGuard && digitalRead(9) == HIGH ) {
				buttonLastDebounceTime = millis();
				currentState = off;
			}
		break;
		case off:
			digitalWrite(12,LOW);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if( buttonBounceGuard && digitalRead(9) == HIGH ) {
				buttonLastDebounceTime = millis();
				currentState = on;
			}
		break;
	}
}
