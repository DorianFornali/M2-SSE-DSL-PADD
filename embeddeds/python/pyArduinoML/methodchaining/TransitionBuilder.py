from pyArduinoML.model.Transition import Transition
from pyArduinoML.model.DigitalCondition import DigitalCondition
from pyArduinoML.model.SIGNAL import Signal
from pyArduinoML.model.SignalTransition import SignalTransition
from pyArduinoML.methodchaining.ConditionTreeBuilder import ConditionTreeBuilder
from pyArduinoML.methodchaining.UndefinedBrick import UndefinedBrick
from pyArduinoML.methodchaining.UndefinedState import UndefinedState


class TransitionBuilder:
    """
    Builder for defining transitions between states.
    """

    def __init__(self, parent, source_state_name):
        """
        Constructor for TransitionBuilder.

        :param parent: TransitionTableBuilder, the parent builder.
        :param source_state_name: str, the name of the source state.
        """
        self.parent = parent
        self.local = SignalTransition()
        source_state = self.parent.find_state(source_state_name)
        if source_state is None:
            raise ValueError(f"Unknown state: [{source_state_name}]")
        source_state.set_transition(self.local)

        

    def when(self, sensor_name=None):
        """
        Start defining a condition tree for the transition.

        :param sensor_name: str, the name of the sensor (optional).
        :return: ConditionTreeBuilder
        """
        self.condition_tree_builder = ConditionTreeBuilder(self)
        if sensor_name:
            self.condition_tree_builder.add_sensor(sensor_name)
        return self.condition_tree_builder

    def go_to(self, target_state_name):
        """
        Define the target state for the transition.

        :param target_state_name: str, the name of the target state.
        :return: TransitionTableBuilder
        """
        target_state = self.parent.find_state(target_state_name)
        if target_state is None:
            raise ValueError(f"Target state '{target_state_name}' not found.")
        self.local.next = target_state
        return self.parent
