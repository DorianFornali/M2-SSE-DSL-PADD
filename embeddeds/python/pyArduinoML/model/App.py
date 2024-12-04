from typing import List
from pyArduinoML.model.NamedElement import NamedElement
from pyArduinoML.model.Brick import Brick
from pyArduinoML.model.Constant import Constant
from pyArduinoML.model.State import State


class App(NamedElement):
    """
    Represents an ArduinoML application containing bricks, states, constants, and an initial state.
    """

    def __init__(self, name: str, bricks: List[Brick], states: List[State], initial: State, constants: List[Constant]):
        """
        Constructor for App.

        :param name: str, the name of the application.
        :param bricks: List[Brick], the list of bricks in the application.
        :param states: List[State], the list of states in the application.
        :param initial: State, the initial state of the application.
        :param constants: List[Constant], optional list of constants in the application.
        """
        

        self._name = name
        self._bricks = bricks
        self._states = states
        self._constants = constants 
        self._initial = initial

    @property
    def name(self) -> str:
        """
        Getter for the name of the application.

        :return: str, the name of the application.
        """
        return self._name

    @name.setter
    def name(self, name: str):
        """
        Setter for the name of the application.

        :param name: str, the name to set.
        """
        self._name = name

    @property
    def bricks(self) -> List[Brick]:
        """
        Getter for the bricks in the application.

        :return: List[Brick], the list of bricks.
        """
        return self._bricks

    @property
    def states(self) -> List[State]:
        """
        Getter for the states in the application.

        :return: List[State], the list of states.
        """
        return self._states

    @property
    def initial(self) -> State:
        """
        Getter for the initial state of the application.

        :return: State, the initial state.
        """
        return self._initial

    @initial.setter
    def initial(self, initial: State):
        """
        Setter for the initial state of the application.

        :param initial: State, the initial state to set.
        """
        
        self._initial = initial

    @property
    def constants(self) -> List[Constant]:
        """
        Getter for the constants in the application.

        :return: List[Constant], the list of constants.
        """
        return self._constants

    def add_constant(self, constant):
        """
        Add a constant globally to the AppBuilder.

        :param constant: Constant, the constant to add.
        """
        if (self.constants is None):
            self.constants = {}
        
        if constant.name in self.constants:
            print(f"Constant with name {constant.name} already exists.")
            return
        self.constants.append(constant)

    def accept(self, visitor):
        visitor.visit_app(self)  