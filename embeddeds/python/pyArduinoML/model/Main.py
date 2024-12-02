__author__ = 'pascalpoizat'

"""
no DSL version of the demo application
"""


def demo():
    from pyArduinoML.model.App import App
    from pyArduinoML.model.Action import Action
    from pyArduinoML.model.DigitalAction import DigitalAction
    from pyArduinoML.model.Actuator import Actuator
    from pyArduinoML.model.Sensor import Sensor
    from pyArduinoML.model.State import State
    from pyArduinoML.model.Transition import Transition
    from pyArduinoML.model.SIGNAL import Signal
    from pyArduinoML.model.DigitalCondition import DigitalCondition
    from pyArduinoML.model.Constant import Constant

    button = Sensor("BUTTON", 9)
    led = Actuator("LED", 12)

    on = State("on")
    on.actions = [DigitalAction(Signal.HIGH, led)]
    off = State("off")
    off.actions = [DigitalAction(Signal.LOW, led)]

    condition_on = DigitalCondition(button, Signal.HIGH)
    condition_off = DigitalCondition(button, Signal.HIGH)


    switchon = Transition(condition_on, on)
    switchoff = Transition(condition_off, off)

    on.add_transition(switchoff)
    off.add_transition(switchon)

    # Define constants 
    app = App(name = "Switch!", bricks = [button, led], states = [off, on], initial = off, constants=[])

    print(app)


if __name__ == '__main__':
    demo()
