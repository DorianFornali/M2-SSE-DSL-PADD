
// Wiring code generated from an ArduinoML model
//  Application name: MultiStateAlarm

long debounce = 200;
enum STATE
{
    init,
    buzzerOn,
    ledOn
};

STATE currentState = init;

bool buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

void setup()
{
    pinMode(13, OUTPUT); // buzzer [Actuator]
    pinMode(12, OUTPUT); // led [Actuator]
    pinMode(9, INPUT);   // button [Sensor]
}
void loop()
{
    switch (currentState)
    {

    case init:
        digitalWrite(13, LOW);
        digitalWrite(12, LOW);
        bool conditionMet = false;
        bool bounceGuard = false;

        bool conditionMet = (digitalRead(9) == LOW);

        bool buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
        if (conditionMet && buttonBounceGuard)
        {
            buttonLastDebounceTime = millis();
            bounceGuard = true;
        }

        if (conditionMet && bounceGuard)
        {
            currentState = buzzerOn;
        }

        break;
    case buzzerOn:
        digitalWrite(13, HIGH);
        bool conditionMet = false;
        bool bounceGuard = false;

        bool conditionMet = (digitalRead(9) == LOW);

        bool buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
        if (conditionMet && buttonBounceGuard)
        {
            buttonLastDebounceTime = millis();
            bounceGuard = true;
        }

        if (conditionMet && bounceGuard)
        {
            currentState = ledOn;
        }

        break;
    case ledOn:
        digitalWrite(12, HIGH);
        digitalWrite(13, LOW);
        bool conditionMet = false;
        bool bounceGuard = false;

        bool conditionMet = (digitalRead(9) == LOW);

        bool buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
        if (conditionMet && buttonBounceGuard)
        {
            buttonLastDebounceTime = millis();
            bounceGuard = true;
        }

        if (conditionMet && bounceGuard)
        {
            currentState = init;
        }

        break;
    }
}
