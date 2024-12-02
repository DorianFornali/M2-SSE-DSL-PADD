digitalSensor "button" onPin 9
digitalActuator "led" pin 11
digitalActuator "buzzer" pin 12

state "initial" means "led" becomes "low" and "buzzer" becomes "low"
state "buzzerOn" means "led" becomes "low" and "buzzer" becomes "high"
state "ledOn" means "led" becomes "high" and "buzzer" becomes "low"

initial "initial"

from "initial" to "buzzerOn" when "button" becomes "high" done "OK"
from "buzzerOn" to "ledOn" when "button" becomes "high" done "OK"
from "ledOn" to "initial" when "button" becomes "high" done "OK"

export "Multi State Alarm"