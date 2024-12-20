// Wiring code generated from an ArduinoML model
// Application name: Dual Check Alarm

long debounce = 200;

enum STATE {pressed, unpressed};
STATE currentState = unpressed;

boolean buttonOneBounceGuard = false;
long buttonOneLastDebounceTime = 0;

boolean buttonTwoBounceGuard = false;
long buttonTwoLastDebounceTime = 0;

void setup(){
  pinMode(9, INPUT);  // buttonOne [Digital Sensor] CONNECT TO D9
  pinMode(10, INPUT);  // buttonTwo [Digital Sensor] CONNECT TO D10
  pinMode(12, OUTPUT); // buzzer [Digital Actuator] CONNECT TO D12
}

void loop() {
	switch(currentState){
		case pressed:
			digitalWrite(12,HIGH);
			buttonOneBounceGuard = millis() - buttonOneLastDebounceTime > debounce;
			buttonTwoBounceGuard = millis() - buttonTwoLastDebounceTime > debounce;
			if( buttonOneBounceGuard && buttonTwoBounceGuard && ( digitalRead(9) == LOW || digitalRead(10) == LOW )) {
				buttonOneLastDebounceTime = millis();
				buttonTwoLastDebounceTime = millis();
				currentState = unpressed;
			}
		break;
		case unpressed:
			digitalWrite(12,LOW);
			buttonOneBounceGuard = millis() - buttonOneLastDebounceTime > debounce;
			buttonTwoBounceGuard = millis() - buttonTwoLastDebounceTime > debounce;
			if( buttonOneBounceGuard && buttonTwoBounceGuard && ( digitalRead(9) == HIGH && digitalRead(10) == HIGH )) {
				buttonOneLastDebounceTime = millis();
				buttonTwoLastDebounceTime = millis();
				currentState = pressed;
			}
		break;
	}
}
