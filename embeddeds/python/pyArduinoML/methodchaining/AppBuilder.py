__author__ = 'pascalpoizat'

from pyArduinoML.model.App import App
from pyArduinoML.model.Actuator import Actuator
from pyArduinoML.model.Sensor import Sensor

from pyArduinoML.methodchaining.StateBuilder import StateBuilder
from pyArduinoML.methodchaining.TransitionTableBuilder import TransitionTableBuilder


class AppBuilder:
    """
    Builder for the application.
    """

    def __init__(self):
        """
        Constructor.

        :param name: String, name of the app
        :return:
        """
        self.the_app: App = None

    @staticmethod
    def application(name):
        """
        Static factory method to start building an application.
        :param name: str, the name of the application.
        :return: AppBuilder instance.
        """
        inst = AppBuilder()
        inst.the_app = App(name=name, bricks=[], states=[], initial=None, constants=[])
        return inst
    
    def build(self):
        """
        Builds the application.

        :return: App, the application
        """
        return self.the_app
    
    def uses(self, brick):
        """
        Adds a brick.

        :param brick: BrickBuilder, builder for the brick
        :return: AppBuilder, the current builder
        """
        self.the_app.bricks.append(brick)
        return self
    
    @staticmethod
    def sensor(name, port):
        return AppBuilder._create_brick(Sensor, name, port)

    @staticmethod
    def actuator(name, port):
        """
        Create an actuator brick.
        :param name: str, the name of the actuator.
        :param port: int, the port number for the actuator.
        :return: Actuator instance.
        """
        return AppBuilder._create_brick(Actuator, name, port)

    @staticmethod
    def _create_brick(cls, name, port):
        """
        Internal helper to create a sensor or actuator with validation.
        :param cls: Class, either Sensor or Actuator.
        :param name: str, the name of the brick.
        :param port: int, the port number.
        :return: Brick (Sensor or Actuator) instance.
        """
        if not name or not name[0].islower():
            raise ValueError(f"Illegal brick name: [{name}]")
        if port < 1 or port > 12:
            raise ValueError(f"Illegal brick port: [{port}]")
        brick = cls(name, port)
        return brick

    def hasForState(self, name):
        """
        Start defining a state in the application.
        :param name: str, the name of the state.
        :return: StateBuilder instance.
        """
        return StateBuilder(self, name)

    def beginTransitionTable(self):
        """
        Start defining the transition table for the application.
        :return: TransitionTableBuilder instance.
        """
        # Create lookup tables for states and sensors
        state_table = {state.name: state for state in self.the_app.states}
        sensor_table = {brick.name: brick for brick in self.the_app.bricks if isinstance(brick, Sensor)}

        return TransitionTableBuilder(self, state_table, sensor_table)

    def constant(self, name, value):
        """
        Adds a constant.

        :param name: String, name of the constant
        :param value: int, value of the constant
        :return: AppBuilder, the current builder
        """
        from pyArduinoML.model.Constant import Constant
        self.constants.append(Constant(name, value))
        return self

    def initial(self, state_name):
        """
        Sets the initial state.

        :param state_name: String, name of the initial state
        :return: self
        """
        for builder in self.states:
            if builder.state == state_name:
                self.initial_state = builder
                return self
        raise ValueError(f"State {state_name} not found in the application.")

    def findActuator(self, name):
        """
        Find an actuator by name in the application.
        :param name: str, the name of the actuator.
        :return: Actuator instance or None.
        """
        for brick in self.the_app.bricks:
            if isinstance(brick, Actuator) and brick.name == name:
                return brick
        return None
