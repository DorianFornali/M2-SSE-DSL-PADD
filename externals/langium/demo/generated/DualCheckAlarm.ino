
// Wiring code generated from an ArduinoML model
//  Application name: DualCheckAlarm

long debounce = 200;
enum STATE
{
    unpressed,
    pressed
};

STATE currentState = unpressed;

bool buttononeBounceGuard = false;
long buttononeLastDebounceTime = 0;

bool buttontwoBounceGuard = false;
long buttontwoLastDebounceTime = 0;

void setup()
{
    pinMode(13, OUTPUT); // buzzer [Actuator]
    pinMode(9, INPUT);   // buttonone [Sensor]
    pinMode(10, INPUT);  // buttontwo [Sensor]
}
void loop()
{
    switch (currentState)
    {

    case unpressed:
        digitalWrite(13, LOW);
        bool conditionMet = false;
        bool bounceGuard = false;

        bool leftCondition = (digitalRead(9) == LOW);

        bool buttononeBounceGuard = millis() - buttononeLastDebounceTime > debounce;
        if (leftCondition && buttononeBounceGuard)
        {
            buttononeLastDebounceTime = millis();
            bounceGuard = true;
        }

        bool rightCondition = (digitalRead(10) == LOW);

        bool buttononeBounceGuard = millis() - buttononeLastDebounceTime > debounce;
        if (rightCondition && buttononeBounceGuard)
        {
            buttononeLastDebounceTime = millis();
            bounceGuard = true;
        }

        conditionMet = leftCondition && rightCondition;

        if (conditionMet && bounceGuard)
        {
            currentState = pressed;
        }

        break;
    case pressed:
        digitalWrite(13, HIGH);
        bool conditionMet = false;
        bool bounceGuard = false;

        bool leftCondition = (digitalRead(9) == HIGH);

        bool buttononeBounceGuard = millis() - buttononeLastDebounceTime > debounce;
        if (leftCondition && buttononeBounceGuard)
        {
            buttononeLastDebounceTime = millis();
            bounceGuard = true;
        }

        bool rightCondition = (digitalRead(10) == HIGH);

        bool buttononeBounceGuard = millis() - buttononeLastDebounceTime > debounce;
        if (rightCondition && buttononeBounceGuard)
        {
            buttononeLastDebounceTime = millis();
            bounceGuard = true;
        }

        conditionMet = leftCondition || rightCondition;

        if (conditionMet && bounceGuard)
        {
            currentState = unpressed;
        }

        break;
    }
}
