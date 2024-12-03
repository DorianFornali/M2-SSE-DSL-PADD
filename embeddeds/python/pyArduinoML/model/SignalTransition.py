from pyArduinoML.model.Sensor import Sensor
from pyArduinoML.model.SIGNAL import Signal
from pyArduinoML.model.Transition import Transition
from pyArduinoML.model.State import State
from pyArduinoML.model.ConditionTree import ConditionTree

class SignalTransition(Transition):
    """
    Represents a transition triggered by a sensor's signal value.
    """

    def __init__(self, sensor: Sensor = None, value: Signal = None, nextstate: State = None, condition: ConditionTree = None):
        """
        Constructor for SignalTransition.

        :param sensor: Sensor, the sensor associated with this transition.
        :param value: Signal, the signal value (HIGH/LOW) to trigger the transition.
        :param nextstate: State, the next state to transition to.
        """
        super().__init__(nextstate)
        self._sensor = sensor
        self._value = value
        self._condition = condition 
    @property
    def sensor(self) -> Sensor:
        """
        Getter for the sensor.

        :return: Sensor, the sensor for this transition.
        """
        return self._sensor

    @sensor.setter
    def sensor(self, sensor: Sensor):
        """
        Setter for the sensor.

        :param sensor: Sensor, the sensor to set.
        """
        self._sensor = sensor

    @property
    def value(self) -> Signal:
        """
        Getter for the signal value.

        :return: Signal, the signal value for this transition.
        """
        return self._value

    @value.setter
    def value(self, value: Signal):
        """
        Setter for the signal value.

        :param value: Signal, the signal value to set.
        """
        self._value = value

    @property
    def condition(self) -> ConditionTree:
        """
        Getter for the condition.

        :return: ConditionTree, the condition for this transition.
        """
        return self._condition

    @condition.setter
    def condition(self, condition: ConditionTree):
        """
        Setter for the condition.

        :param condition: ConditionTree, the condition to set.
        """
        self._condition = condition

    def accept(self, visitor):
        """
        Accept a visitor for code generation.

        :param visitor: The visitor instance.
        """
        visitor.visit_signal_transition(self)
