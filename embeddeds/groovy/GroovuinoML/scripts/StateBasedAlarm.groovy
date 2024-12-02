digitalSensor "button" onPin 9
digitalActuator "led" pin 11

state "on" means "led" becomes "high"
state "off" means "led" becomes "low"

initial "off"

from "on" to "off" when "button" becomes "high" done "OK"
from "off" to "on" when "button" becomes "high" done "OK"

export "State Based Alarm"