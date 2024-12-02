digitalSensor "button" onPin 8
digitalSensor "button2" onPin 9
digitalActuator "led" pin 10
digitalActuator "buzzer" pin 11

state "pressed" means "led" becomes "high" and "buzzer" becomes "high"
state "unpressed" means "led" becomes "low" and "buzzer" becomes "low"

initial "unpressed"

from "pressed" to "unpressed" when "button" becomes "high" and "button2" becomes "high" done "OK"
from "unpressed" to "pressed" when "button" becomes "low" done "OK"

export "Simple Alarm"