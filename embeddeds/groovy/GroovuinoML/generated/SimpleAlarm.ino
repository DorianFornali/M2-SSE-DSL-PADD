// Wiring code generated from an ArduinoML model
// Application name: Simple Alarm

long debounce = 200;

enum STATE {pressed, unpressed};
STATE currentState = unpressed;

boolean buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

boolean button2BounceGuard = false;
long button2LastDebounceTime = 0;

void setup(){
  pinMode(9, INPUT);  // button [Sensor]
  pinMode(10, INPUT);  // button2 [Sensor]
  pinMode(12, OUTPUT); // led [Actuator]
  pinMode(13, OUTPUT); // buzzer [Actuator]
}

void loop() {
	switch(currentState){
		case pressed:
			digitalWrite(12,HIGH);
			digitalWrite(13,HIGH);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			button2BounceGuard = millis() - button2LastDebounceTime > debounce;
			if( buttonBounceGuard && button2BounceGuard && ( digitalRead(9) == HIGH && digitalRead(10) == HIGH )) {
				buttonLastDebounceTime = millis();
				button2LastDebounceTime = millis();
				currentState = unpressed;
			}
		break;
		case unpressed:
			digitalWrite(12,LOW);
			digitalWrite(13,LOW);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if( buttonBounceGuard && digitalRead(9) == LOW ) {
				buttonLastDebounceTime = millis();
				currentState = pressed;
			}
		break;
	}
}
