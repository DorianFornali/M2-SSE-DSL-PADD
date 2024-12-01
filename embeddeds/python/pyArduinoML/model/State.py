from typing import List
from pyArduinoML.model.NamedElement import NamedElement
from pyArduinoML.model.Action import Action


class State(NamedElement):
    """
    Represents a state in the ArduinoML behavioral model.
    """

    def __init__(self, name: str = None):
        """
        Constructor for State.

        :param name: str, the name of the state.
        """
        self._name = name
        self._actions = []
        self._transition = None

    @property
    def name(self) -> str:
        """
        Getter for the name of the state.

        :return: str, the name of the state.
        """
        return self._name

    @name.setter
    def name(self, name: str):
        """
        Setter for the name of the state.

        :param name: str, the name to set.
        """
        self._name = name

    @property
    def actions(self) -> List[Action]:
        """
        Getter for the actions associated with the state.

        :return: List[Action], the list of actions.
        """
        return self._actions

    @actions.setter
    def actions(self, actions: List[Action]):
        """
        Setter for the actions associated with the state.

        :param actions: List[Action], the list of actions to set.
        """
        self._actions = actions

    @property
    def transition(self):
        """
        Getter for the transitions associated with the state.

        :return: List[Transition], the list of transitions.
        """
        return self._transition

    def set_transition(self, transition):
        """
        Adds a transition to the state.

        :param transition: Transition, the transition to add.
        """
        self._transition = transition

    def accept(self, visitor):
        visitor.visit_state(self)