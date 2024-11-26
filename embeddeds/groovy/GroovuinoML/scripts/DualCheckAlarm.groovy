sensor "buttonOne" onPin 9
sensor "buttonTwo" onPin 10
actuator "buzzer" pin 13

state "pressed" means "buzzer" becomes "high"
state "unpressed" means "buzzer" becomes "low"

initial "unpressed"

from "pressed" to "unpressed" when "buttonOne" becomes "low" or "buttonTwo" becomes "low" done "OK"
from "unpressed" to "pressed" when "buttonOne" becomes "high" and "buttonTwo" becomes "high" done "OK"

export "Dual Check Alarm"