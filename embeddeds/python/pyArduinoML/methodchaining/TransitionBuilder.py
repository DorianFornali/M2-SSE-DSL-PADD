from pyArduinoML.model.Transition import Transition
from pyArduinoML.model.DigitalCondition import DigitalCondition
from pyArduinoML.model.SIGNAL import Signal
from pyArduinoML.model.SignalTransition import SignalTransition
from pyArduinoML.methodchaining.UndefinedBrick import UndefinedBrick
from pyArduinoML.methodchaining.UndefinedState import UndefinedState


class TransitionBuilder:
    """
    Builder for defining transitions between states.
    """

    def __init__(self, parent, source_state_name):
        """
        Constructor for TransitionBuilder.

        :param parent: TransitionTableBuilder, the parent builder.
        :param source_state_name: str, the name of the source state for the transition.
        """
        self.parent = parent
        self.local = SignalTransition() 
        source_state = self.parent.find_state(source_state_name).set_transition(self.local)

        

    def when(self, sensor_name):
        """
        Define the sensor used in the transition condition.

        :param sensor_name: str, the name of the sensor.
        :return: self
        """
        sensor = self.parent.find_sensor(sensor_name)
        self.local.set_sensor(sensor)
        return self

    def isHigh(self):
        """
        Set the condition to trigger when the sensor signal is HIGH.

        :return: self
        """
        self.local.set_value(Signal.HIGH)
        return self

    def isLow(self):
        """
        Set the condition to trigger when the sensor signal is LOW.

        :return: self
        """
        
        self.local.set_value(Signal.LOW) 
        return self

    def goTo(self, target_state_name):
        """
        Define the target state for the transition.

        :param target_state_name: str, the name of the target state.
        :return: TransitionTableBuilder
        """
        target_state = self.parent.find_state(target_state_name)
        self.local.next = target_state
        return self.parent
