class Constant:
    """
    Represents a named constant with a numeric value.
    """

    def __init__(self, name: str, value: float):
        """
        Constructor for Constant.

        :param name: str, the name of the constant.
        :param value: float, the numeric value of the constant.
        """
        self._name = name
        self._value = value

    @property
    def name(self) -> str:
        """
        Getter for the name of the constant.

        :return: str, the name of the constant.
        """
        return self._name

    @name.setter
    def name(self, name: str):
        """
        Setter for the name of the constant.

        :param name: str, the name to set.
        """
        self._name = name

    @property
    def value(self) -> float:
        """
        Getter for the value of the constant.

        :return: float, the value of the constant.
        """
        return self._value

    @value.setter
    def value(self, value: float):
        """
        Setter for the value of the constant.

        :param value: float, the numeric value to set.
        """
        self._value = value

    def accept(self, visitor):
        """
        Accept a visitor to visit this Constant.

        :param visitor: a visitor.
        """
        visitor.visit_constant(self)