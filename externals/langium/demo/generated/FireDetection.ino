
// Wiring code generated from an ArduinoML model
//  Application name: FireDetection

long debounce = 200;
enum STATE
{
    idle,
    alarming
};

STATE currentState = idle;

bool temperatureBounceGuard = false;
long temperatureLastDebounceTime = 0;

void setup()
{
    pinMode(1, INPUT);   // temperature [Sensor]
    pinMode(11, OUTPUT); // alarm [Actuator]
}
void loop()
{
    switch (currentState)
    {

    case idle:
        digitalWrite(11, LOW);
        bool conditionMet = false;
        bool bounceGuard = false;

        bool conditionMet = (analogRead(1) > 50.7);

        bool temperatureBounceGuard = millis() - temperatureLastDebounceTime > debounce;
        if (conditionMet && temperatureBounceGuard)
        {
            temperatureLastDebounceTime = millis();
            bounceGuard = true;
        }

        if (conditionMet && bounceGuard)
        {
            currentState = alarming;
        }

        break;
    case alarming:
        digitalWrite(11, HIGH);
        bool conditionMet = false;
        bool bounceGuard = false;

        bool conditionMet = (analogRead(1) <= 50.7);

        bool temperatureBounceGuard = millis() - temperatureLastDebounceTime > debounce;
        if (conditionMet && temperatureBounceGuard)
        {
            temperatureLastDebounceTime = millis();
            bounceGuard = true;
        }

        if (conditionMet && bounceGuard)
        {
            currentState = idle;
        }

        break;
    }
}
