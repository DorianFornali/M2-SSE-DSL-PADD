from pyArduinoML.methodchaining.TransitionBuilder import TransitionBuilder

class TransitionTableBuilder:
    """
    Builder for defining the transition table.
    """

    def __init__(self, parent, states: dict, sensors: dict, constants: dict):
        """
        Constructor for TransitionTableBuilder.

        :param parent: AppBuilder, the parent builder.
        :param states: dict, mapping of state names to State instances.
        :param sensors: dict, mapping of sensor names to Sensor instances.
        """
        self.parent = parent
        self.states = states
        self.sensors = sensors
        self.constants = constants 

    def from_(self, state_name):
        """
        Start defining a transition from a given state.

        :param state_name: str, the name of the state to transition from.
        :return: TransitionBuilder instance.
        """

        return TransitionBuilder(self, state_name)

    def endTransitionTable(self):
        """
        End the transition table definition and return to the parent builder.

        :return: AppBuilder instance.
        """
        return self.parent

    def find_sensor(self, sensor_name):
        """
        Find a sensor by name.

        :param sensor_name: str, the name of the sensor.
        :return: Sensor instance.
        :raises ValueError: if the sensor is not found.
        """
        sensor = self.sensors.get(sensor_name)
        if sensor is None:
            raise ValueError(f"Unknown sensor: [{sensor_name}]")
        return sensor

    def find_state(self, state_name):
        """
        Find a state by name.

        :param state_name: str, the name of the state.
        :return: State instance.
        :raises ValueError: if the state is not found.
        """
        state = self.states.get(state_name)
        if state is None:
            raise ValueError(f"Unknown state: [{state_name}]")
        return state
    

    def is_constant (self, constant_name):
        """
        Check if a constant exists.

        :param constant_name: str, the name of the constant.
        :return: bool, whether the constant exists.
        """
        return constant_name in self.constants



    def find_constant (self, constant_name):
        """
        Find a constant by name.

        :param constant_name: str, the name of the constant.
        :return: Constant instance.
        :raises ValueError: if the constant is not found.
        """
        constant = self.constants.get(constant_name)
        if constant is None:
            raise ValueError(f"Unknown constant: [{constant_name}]")
        return constant

    def value_already_present(self, value):
        """
        Check if a constant with the given value already exists.

        :param value: float, the value to check.
        :return: Constant instance or None.
        """
        for constant in self.constants.values():
            if constant.value == value:
                return constant
        return None
    
    def add_constant(self, constant):
        """
        Add a constant to the list of known constants.

        :param constant: Constant, the constant to add.
        """
        self.constants[constant.name] = constant