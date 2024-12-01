from pyArduinoML.model.ConditionTree import ConditionTree
from pyArduinoML.model.Sensor import Sensor
from abc import ABC, abstractmethod

class Condition (ConditionTree):
    def __init__(self, sensor = None):
        self._sensor = sensor 
    
    @property
    def sensor(self) -> Sensor:
        """
        Getter for the sensor.

        :return: Sensor, the sensor associated with this condition
        """
        return self._sensor
    
    @sensor.setter
    def sensor(self, sensor: Sensor):
        """
        Setter for the sensor.

        :param sensor: Sensor, the sensor to associate with this condition
        """
        self._sensor = sensor

    @abstractmethod
    def evaluate(self) -> bool:
        """
        Abstract method to evaluate the condition.
        Must be implemented by subclasses.
        """
        pass