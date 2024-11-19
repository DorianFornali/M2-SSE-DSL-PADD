
//Wiring code generated from an ArduinoML model
// Application name: SimpleAlarm

long debounce = 200;
enum STATE {unpressed, pressed};

STATE currentState = unpressed;

bool buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

            

	void setup(){
		pinMode(12, OUTPUT); // led [Actuator]
		pinMode(13, OUTPUT); // buzzer [Actuator]
		pinMode(9, INPUT); // button [Sensor]
	}
	void loop() {
			switch(currentState){

				case unpressed:
					digitalWrite(12,LOW);
					digitalWrite(13,LOW);
		 			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
					if( digitalRead(9) == HIGH && buttonBounceGuard) {
						buttonLastDebounceTime = millis();
						currentState = pressed;
					}
		
				break;
				case pressed:
					digitalWrite(12,HIGH);
					digitalWrite(13,HIGH);
		 			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
					if( digitalRead(9) == HIGH && buttonBounceGuard) {
						buttonLastDebounceTime = millis();
						currentState = unpressed;
					}
		
				break;
		}
	}
	
