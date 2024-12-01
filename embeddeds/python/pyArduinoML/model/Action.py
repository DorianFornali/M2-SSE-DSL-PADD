from abc import ABC, abstractmethod
from pyArduinoML.model.Actuator import Actuator


class Action(ABC):
    """
    Abstract base class for an action to be performed on an actuator.
    """

    def __init__(self, actuator: Actuator = None):
        """
        Constructor for Action.

        :param actuator: Actuator, the actuator associated with the action.
        """
        self._actuator = actuator

    @property
    def actuator(self) -> Actuator:
        """
        Getter for the actuator.

        :return: Actuator, the actuator associated with the action.
        """
        return self._actuator

    @actuator.setter
    def actuator(self, actuator: Actuator):
        """
        Setter for the actuator.

        :param actuator: Actuator, the actuator associated with the action.
        """
        if not isinstance(actuator, Actuator):
            raise TypeError("actuator must be an instance of Actuator")
        self._actuator = actuator
