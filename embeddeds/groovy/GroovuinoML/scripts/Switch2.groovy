digitalSensor "button" pin 8
digitalActuator "led1" pin 9
digitalActuator "led2" pin 10
digitalActuator "led3" pin 11

state "on" means led1 becomes high
state "off" means led1 becomes low and led2 becomes low and led3 becomes low

initial off

from on to off when button becomes high done "OK"
from off to on when button becomes high done "OK"

export "Switch!"