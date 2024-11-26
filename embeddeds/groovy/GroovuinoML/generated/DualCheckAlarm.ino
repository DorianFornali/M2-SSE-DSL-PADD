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
  pinMode(9, INPUT);  // buttonOne [Sensor]
  pinMode(10, INPUT);  // buttonTwo [Sensor]
  pinMode(13, OUTPUT); // buzzer [Actuator]
}

void loop() {
	switch(currentState){
		case pressed:
			digitalWrite(13,HIGH);
			buttonOneBounceGuard = millis() - buttonOneLastDebounceTime > debounce;
			buttonTwoBounceGuard = millis() - buttonTwoLastDebounceTime > debounce;
			if( buttonOneBounceGuard && buttonTwoBounceGuard && ( digitalRead(9) == LOW || digitalRead(10) == LOW )) {
				buttonOneLastDebounceTime = millis();
				buttonTwoLastDebounceTime = millis();
				currentState = unpressed;
			}
		break;
		case unpressed:
			digitalWrite(13,LOW);
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
