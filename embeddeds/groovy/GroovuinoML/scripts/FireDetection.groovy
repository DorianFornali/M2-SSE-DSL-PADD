analogSensor "temperature" onPin 1
digitalActuator "buzzer" pin 11

state "idle" means "buzzer" becomes "low"
state "alarming" means "buzzer" becomes "high"

initial "idle"

from "idle" to "alarming" when "temperature" GT 50.7 done "OK"
from "alarming" to "idle" when "temperature" LEQ 50.7 done "OK"

export "Fire Detection"