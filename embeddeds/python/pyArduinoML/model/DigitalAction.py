from pyArduinoML.model.Action import Action
from pyArduinoML.model.SIGNAL import Signal
from pyArduinoML.model.Actuator import Actuator


class DigitalAction(Action):
    """
    Represents a digital action to be performed, using a signal value (HIGH or LOW) and an actuator.
    """

    def __init__(self, value: Signal, actuator: Actuator):
        """
        Constructor for DigitalAction.

        :param value: Signal, the signal value (HIGH or LOW) associated with the action.
        :param actuator: Actuator, the actuator associated with the action.
        """
        super().__init__(actuator)  # Initialize the base Action class with the actuator
        self._value = value

    @property
    def value(self) -> Signal:
        """
        Getter for the signal value.

        :return: Signal, the signal value associated with the action.
        """
        return self._value

    @value.setter
    def value(self, value: Signal):
        """
        Setter for the signal value.

        :param value: Signal, the signal value (HIGH or LOW) associated with the action.
        """
        if not isinstance(value, Signal):
            raise TypeError("value must be an instance of Signal (HIGH or LOW)")
        self._value = value

    def accept(self, visitor):
        """
        Accept a visitor and call the appropriate visit method.

        :param visitor: The visitor instance.
        """
        visitor.visit_action(self)