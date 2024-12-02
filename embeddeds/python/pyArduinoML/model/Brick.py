__author__ = 'pascalpoizat'

from pyArduinoML.model.NamedElement import NamedElement

class Brick(NamedElement):
    """
    Abstraction for bricks.

    """

    def __init__(self, name: str = None, pin: int = None):
        """
        Constructor for Brick.

        :param name: str, the name of the brick.
        :param pin: int, the pin number associated with the brick.
        """
        self._name = name
        self._pin = pin

    def accept(self, visitor):
        visitor.visit_brick(self)

    @property
    def pin(self) -> int:
        """
        Getter for the pin number.

        :return: int, the pin number.
        """
        return self._pin

    @pin.setter
    def pin(self, pin: int):
        """
        Setter for the pin number.

        :param pin: int, the pin number to set.
        """
        self._pin = pin

    @property
    def name(self) -> str:
        """
        Getter for the name of the brick.

        :return: str, the name of the brick.
        """
        return self._name

    @name.setter
    def name(self, name: str):
        """
        Setter for the name of the brick.

        :param name: str, the name to set.
        """
        self._name = name
