// Wiring code generated from an ArduinoML model
// Application name: Multi State Alarm

long debounce = 200;

enum STATE {initial, buzzerOn, ledOn};
STATE currentState = initial;

boolean buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

void setup(){
  pinMode(9, INPUT);  // button [Digital Sensor] CONNECT TO D9
  pinMode(11, OUTPUT); // led [Digital Actuator] CONNECT TO D11
  pinMode(12, OUTPUT); // buzzer [Digital Actuator] CONNECT TO D12
}

void loop() {
	switch(currentState){
		case initial:
			digitalWrite(11,LOW);
			digitalWrite(12,LOW);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if( buttonBounceGuard && digitalRead(9) == HIGH ) {
				buttonLastDebounceTime = millis();
				currentState = buzzerOn;
			}
		break;
		case buzzerOn:
			digitalWrite(11,LOW);
			digitalWrite(12,HIGH);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if( buttonBounceGuard && digitalRead(9) == HIGH ) {
				buttonLastDebounceTime = millis();
				currentState = ledOn;
			}
		break;
		case ledOn:
			digitalWrite(11,HIGH);
			digitalWrite(12,LOW);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if( buttonBounceGuard && digitalRead(9) == HIGH ) {
				buttonLastDebounceTime = millis();
				currentState = initial;
			}
		break;
	}
}
