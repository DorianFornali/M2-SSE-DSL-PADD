from pyArduinoML.model.DigitalAction import DigitalAction
from pyArduinoML.model.SIGNAL import Signal


class InstructionBuilder:
    """
    Builder for defining instructions (actions) for actuators within a state.
    """

    def __init__(self, state_builder, actuator_name):
        """
        Constructor for InstructionBuilder.

        :param state_builder: StateBuilder, the parent builder.
        :param actuator_name: str, the name of the actuator.
        """
        self.state_builder = state_builder
        self.actuator_name = actuator_name
        self.signal = None

    def toHigh(self):
        """
        Set the actuator to HIGH in this instruction.

        :return: StateBuilder, the parent builder for chaining.
        """
        actuator = self.state_builder.parent.findActuator(self.actuator_name)
        if not actuator:
            raise ValueError(f"Actuator {self.actuator_name} not found.")
        action = DigitalAction(Signal.HIGH, actuator)
        self.state_builder.local.actions.append(action)
        return self.state_builder

    def toLow(self):
        """
        Set the actuator to LOW in this instruction.

        :return: StateBuilder, the parent builder for chaining.
        """
        actuator = self.state_builder.parent.findActuator(self.actuator_name)
        if not actuator:
            raise ValueError(f"Actuator {self.actuator_name} not found.")
        action = DigitalAction(Signal.LOW, actuator)
        self.state_builder.local.actions.append(action)
        return self.state_builder
