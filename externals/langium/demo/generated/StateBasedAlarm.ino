
// Wiring code generated from an ArduinoML model
//  Application name: StateBasedAlarm

long debounce = 200;
enum STATE
{
    on,
    off
};

STATE currentState = off;

bool buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

void setup()
{
    pinMode(12, OUTPUT); // led [Actuator]
    pinMode(9, INPUT);   // button [Sensor]
}
void loop()
{
    switch (currentState)
    {

    case on:
        digitalWrite(12, HIGH);
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
            currentState = off;
        }

        break;
    case off:
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
            currentState = on;
        }

        break;
    }
}
