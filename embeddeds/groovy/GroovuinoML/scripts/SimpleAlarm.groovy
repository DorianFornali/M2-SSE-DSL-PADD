sensor "button" onPin 9
sensor "button2" onPin 10
actuator "led" pin 12
actuator "buzzer" pin 13

state "pressed" means "led" becomes "high" and "buzzer" becomes "high"
state "unpressed" means "led" becomes "low" and "buzzer" becomes "low"

initial "unpressed"

from "pressed" to "unpressed" when "button" becomes "high" and "button2" becomes "high" done "OK"
from "unpressed" to "pressed" when "button" becomes "low" done "OK"

export "Simple Alarm"