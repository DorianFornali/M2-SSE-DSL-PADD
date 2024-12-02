__author__ = 'pascalpoizat'

from pyArduinoML.model.Brick import Brick

class Sensor(Brick):

    def accept(self, visitor):
        visitor.visit_sensor(self)
