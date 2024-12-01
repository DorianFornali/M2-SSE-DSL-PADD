__author__ = 'pascalpoizat'
from pyArduinoML.model.State import State
from pyArduinoML.model.ConditionTree import ConditionTree

class Transition :
    """
    A transition between two states.
    """

    def __init__(self, nextstate: State = None):
        """
        Constructor.

        :param sensor: Sensor, sensor which value is checked to trigger the transition
        :param value: SIGNAL, value that the sensor must have to trigger the transition
        :param nextstate: State, state to change to when the transition is triggered
        :return:
        """
        self._nextstate = nextstate
    
    @property
    def next(self) -> State:
        """
        Getter for the next state.

        :return: State, the next state.
        """
        return self._nextstate

    @next.setter
    def next(self, nextstate: State):
        """
        Setter for the next state.

        :param next_state: State, the state to transition to.
        """
        self._nextstate = nextstate

    