from pyArduinoML.model.State import State
from pyArduinoML.methodchaining.InstructionBuilder import InstructionBuilder


class StateBuilder:
    """
    Builder for defining states within the application.
    """

    def __init__(self, parent, name):
        """
        Constructor for StateBuilder.

        :param parent: AppBuilder, the parent builder for the application.
        :param name: str, the name of the state.
        """
        self.parent = parent
        self.local = State(name)

    def setting(self, actuator_name):
        """
        Define an instruction to set an actuator's state in this state.

        :param actuator_name: str, the name of the actuator.
        :return: InstructionBuilder, to build the instruction.
        """
        return InstructionBuilder(self, actuator_name)

    def initial(self):
        """
        Mark this state as the initial state of the application.

        :return: self
        """
        self.parent.the_app.initial = self.local
        return self

    def endState(self):
        """
        Finalize this state and add it to the application.

        :return: AppBuilder, the parent builder for chaining.
        """
        self.parent.the_app.states.append(self.local)
        return self.parent
