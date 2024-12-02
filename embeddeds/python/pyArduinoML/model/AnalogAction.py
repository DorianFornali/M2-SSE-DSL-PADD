from pyArduinoML.model.Action import Action
from pyArduinoML.model.Constant import Constant


class AnalogAction(Action):
    """
    Represents an analog action to be performed, using a constant value.
    """

    def __init__(self, value: Constant = None):
        """
        Constructor for AnalogAction.

        :param value: Constant, the constant value associated with the action.
        """
        self._value = value

    @property
    def value(self) -> Constant:
        """
        Getter for the constant value.

        :return: Constant, the constant value associated with the action.
        """
        return self._value

    @value.setter
    def value(self, value: Constant):
        """
        Setter for the constant value.

        :param value: Constant, the constant value associated with the action.
        """
        if not isinstance(value, Constant):
            raise TypeError("value must be an instance of Constant")
        self._value = value
