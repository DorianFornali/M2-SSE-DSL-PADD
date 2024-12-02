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
  pinMode(8, INPUT);  // button [Digital Sensor] CONNECT TO D8
  pinMode(9, INPUT);  // button2 [Digital Sensor] CONNECT TO D9
  pinMode(10, OUTPUT); // led [Digital Actuator] CONNECT TO D10
  pinMode(11, OUTPUT); // buzzer [Digital Actuator] CONNECT TO D11
}

void loop() {
	switch(currentState){
		case pressed:
			digitalWrite(10,HIGH);
			digitalWrite(11,HIGH);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			button2BounceGuard = millis() - button2LastDebounceTime > debounce;
			if( buttonBounceGuard && button2BounceGuard && ( digitalRead(8) == HIGH && digitalRead(9) == HIGH )) {
				buttonLastDebounceTime = millis();
				button2LastDebounceTime = millis();
				currentState = unpressed;
			}
		break;
		case unpressed:
			digitalWrite(10,LOW);
			digitalWrite(11,LOW);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if( buttonBounceGuard && digitalRead(8) == LOW ) {
				buttonLastDebounceTime = millis();
				currentState = pressed;
			}
		break;
	}
}
