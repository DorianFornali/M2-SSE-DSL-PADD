digitalSensor "buttonOne" done "OK"
digitalSensor "buttonTwo" done "OK"
digitalActuator "buzzer"  done "OK"

state "pressed" means "buzzer" becomes "high"
state "unpressed" means "buzzer" becomes "low"

initial "unpressed"

from "pressed" to "unpressed" when "buttonOne" becomes "low" or "buttonTwo" becomes "low" done "OK"
from "unpressed" to "pressed" when "buttonOne" becomes "high" and "buttonTwo" becomes "high" done "OK"

export "Auto Pin Allocation"