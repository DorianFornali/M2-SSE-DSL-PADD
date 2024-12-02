// Wiring code generated from an ArduinoML model
// Application name: Auto Pin Allocation

long debounce = 200;

enum STATE {pressed, unpressed};
STATE currentState = unpressed;

boolean buttonOneBounceGuard = false;
long buttonOneLastDebounceTime = 0;

boolean buttonTwoBounceGuard = false;
long buttonTwoLastDebounceTime = 0;

void setup(){
  pinMode(8, INPUT);  // buttonOne [Digital Sensor] CONNECT TO D8
  pinMode(9, INPUT);  // buttonTwo [Digital Sensor] CONNECT TO D9
  pinMode(10, OUTPUT); // buzzer [Digital Actuator] CONNECT TO D10
}

void loop() {
	switch(currentState){
		case pressed:
			digitalWrite(10,HIGH);
			buttonOneBounceGuard = millis() - buttonOneLastDebounceTime > debounce;
			buttonTwoBounceGuard = millis() - buttonTwoLastDebounceTime > debounce;
			if( buttonOneBounceGuard && buttonTwoBounceGuard && ( digitalRead(8) == LOW || digitalRead(9) == LOW )) {
				buttonOneLastDebounceTime = millis();
				buttonTwoLastDebounceTime = millis();
				currentState = unpressed;
			}
		break;
		case unpressed:
			digitalWrite(10,LOW);
			buttonOneBounceGuard = millis() - buttonOneLastDebounceTime > debounce;
			buttonTwoBounceGuard = millis() - buttonTwoLastDebounceTime > debounce;
			if( buttonOneBounceGuard && buttonTwoBounceGuard && ( digitalRead(8) == HIGH && digitalRead(9) == HIGH )) {
				buttonOneLastDebounceTime = millis();
				buttonTwoLastDebounceTime = millis();
				currentState = pressed;
			}
		break;
	}
}
