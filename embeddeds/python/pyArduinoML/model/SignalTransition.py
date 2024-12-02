from pyArduinoML.model.Transition import Transition
from pyArduinoML.model.SIGNAL import Signal
from pyArduinoML.model.Sensor import Sensor


class SignalTransition(Transition):
    """
    Represents a transition triggered by a sensor's signal value.
    """

    def __init__(self):
        """
        Constructor for SignalTransition.
        """
        super().__init__()
        self.sensor = None  # Sensor associated with the transition
        self.value = None   # Signal value (HIGH/LOW) triggering the transition

    def get_sensor(self):
        """
        Getter for the sensor.
        :return: Sensor instance.
        """
        return self.sensor

    def set_sensor(self, sensor):
        """
        Setter for the sensor.
        :param sensor: Sensor instance.
        """
        if not isinstance(sensor, Sensor):
            raise TypeError("sensor must be an instance of Sensor")
        self.sensor = sensor

    def get_value(self):
        """
        Getter for the signal value.
        :return: Signal instance (HIGH/LOW).
        """
        return self.value

    def set_value(self, value):
        """
        Setter for the signal value.
        :param value: Signal instance (HIGH/LOW).
        """
        if not isinstance(value, Signal):
            raise TypeError("value must be an instance of Signal")
        self.value = value

    def accept(self, visitor):
        visitor.visit_signal_transition(self)

    
