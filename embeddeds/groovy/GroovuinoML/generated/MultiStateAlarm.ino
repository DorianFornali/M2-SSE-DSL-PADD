// Wiring code generated from an ArduinoML model
// Application name: Multi State Alarm

long debounce = 200;

enum STATE {initial, buzzerOn, ledOn};
STATE currentState = initial;

boolean buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

void setup(){
  pinMode(9, INPUT);  // button [Sensor]
  pinMode(12, OUTPUT); // led [Actuator]
  pinMode(13, OUTPUT); // buzzer [Actuator]
}

void loop() {
	switch(currentState){
		case initial:
			digitalWrite(12,LOW);
			digitalWrite(13,LOW);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if( buttonBounceGuard && digitalRead(9) == HIGH ) {
				buttonLastDebounceTime = millis();
				currentState = buzzerOn;
			}
		break;
		case buzzerOn:
			digitalWrite(12,LOW);
			digitalWrite(13,HIGH);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if( buttonBounceGuard && digitalRead(9) == HIGH ) {
				buttonLastDebounceTime = millis();
				currentState = ledOn;
			}
		break;
		case ledOn:
			digitalWrite(12,HIGH);
			digitalWrite(13,LOW);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if( buttonBounceGuard && digitalRead(9) == HIGH ) {
				buttonLastDebounceTime = millis();
				currentState = initial;
			}
		break;
	}
}
