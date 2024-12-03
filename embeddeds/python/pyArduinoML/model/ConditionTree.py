from abc import ABC, abstractmethod
from pyArduinoML.model.Sensor import Sensor
from typing import List

class ConditionTree(ABC):
    @abstractmethod 
    def getSensors():
        pass 
    
    @abstractmethod
    def evaluate(self) -> bool:
        pass 