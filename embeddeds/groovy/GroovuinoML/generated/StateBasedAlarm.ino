// Wiring code generated from an ArduinoML model
// Application name: State Based Alarm

long debounce = 200;

enum STATE {on, off};
STATE currentState = off;

boolean buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

void setup(){
  pinMode(9, INPUT);  // button [Digital Sensor] CONNECT TO D9
  pinMode(11, OUTPUT); // led [Digital Actuator] CONNECT TO D11
}

void loop() {
	switch(currentState){
		case on:
			digitalWrite(11,HIGH);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if( buttonBounceGuard && digitalRead(9) == HIGH ) {
				buttonLastDebounceTime = millis();
				currentState = off;
			}
		break;
		case off:
			digitalWrite(11,LOW);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if( buttonBounceGuard && digitalRead(9) == HIGH ) {
				buttonLastDebounceTime = millis();
				currentState = on;
			}
		break;
	}
}
