from pyArduinoML.model.Condition import Condition
from pyArduinoML.model.Constant import Constant
from pyArduinoML.model.COMPARATOR import Comparator
from pyArduinoML.model.Sensor import Sensor


class AnalogCondition(Condition):
    """
    Represents an analog condition based on a sensor's value, a constant, and a comparator.
    """

    def __init__(self, value: Constant = None, comparator: Comparator = None):
        """
        Constructor for AnalogCondition.

        :param value: Constant, the constant value to compare the sensor's reading against
        :param comparator: Comparator, the comparator used for the condition (e.g., <, >, ==)
        """
        super().__init__()
        self._value = value
        self._comparator = comparator

    @property
    def value(self) -> Constant:
        """
        Getter for the constant value.

        :return: Constant, the constant value
        """
        return self._value

    @value.setter
    def value(self, value: Constant):
        """
        Setter for the constant value.

        :param value: Constant, the constant value
        """
        if not isinstance(value, Constant):
            raise TypeError("value must be an instance of Constant")
        self._value = value

    @property
    def comparator(self) -> Comparator:
        """
        Getter for the comparator.

        :return: Comparator, the comparator
        """
        return self._comparator

    @comparator.setter
    def comparator(self, comparator: Comparator):
        """
        Setter for the comparator.

        :param comparator: Comparator, the comparator to use
        """
        if not isinstance(comparator, Comparator):
            raise TypeError("comparator must be an instance of Comparator")
        self._comparator = comparator

    def getSensors(self):
        return [self.sensor]

    def evaluate(self) -> bool:
        """
        Evaluates the analog condition.

        :return: bool, whether the condition is satisfied.
        """
        if self.comparator == Comparator.LT:
            return self.sensor.value < self.value.value
        elif self.comparator == Comparator.GT:
            return self.sensor.value > self.value.value
        elif self.comparator == Comparator.EQ:
            return self.sensor.value == self.value.value
        elif self.comparator == Comparator.NEQ:
            return self.sensor.value != self.value.value
        elif self.comparator == Comparator.LEQ:
            return self.sensor.value <= self.value.value
        elif self.comparator == Comparator.GEQ:
            return self.sensor.value >= self.value.value
        else:
            raise ValueError("Invalid comparator")

    def accept(self, visitor):
        visitor.visit_analog_condition(self)