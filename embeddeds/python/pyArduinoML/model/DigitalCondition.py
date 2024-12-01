from pyArduinoML.model.Condition import Condition
from pyArduinoML.model.SIGNAL import Signal
from pyArduinoML.model.Sensor import Sensor


class DigitalCondition(Condition):
    """
    Represents a digital condition based on a signal value.
    """

    def __init__(self, sensor: Sensor, value: Signal):
        """
        Constructor for DigitalCondition.

        :param value: Signal, the signal value (HIGH or LOW)
        """
        super().__init__(sensor)
        self._value = value

    @property
    def value(self) -> Signal:
        """
        Getter for the signal value.

        :return: Signal, the signal value (HIGH or LOW)
        """
        return self._value

    @value.setter
    def value(self, value: Signal):
        """
        Setter for the signal value.

        :param value: Signal, the signal value (HIGH or LOW)
        """
        if not isinstance(value, Signal):
            raise TypeError("value must be an instance of Signal")
        self._value = value
    
    def evaluate(self) -> bool:
        """
        Evaluates the digital condition.

        :return: bool, whether the condition is satisfied.
        """
        return self.sensor.value == self.value
    

    def __str__(self):
        """
        String representation of the DigitalCondition.

        :return: str, a description of the condition.
        """
        return f"DigitalCondition(sensor={self.sensor.name}, value={self.value})"
