__author__ = 'pascalpoizat'

from pyArduinoML.model.Brick import Brick

class Actuator(Brick):
    """
    An actuator.

    """
    def accept(self, visitor):
        visitor.visit_actuator(self)
