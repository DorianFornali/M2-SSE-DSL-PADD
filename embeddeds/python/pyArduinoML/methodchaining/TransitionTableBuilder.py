from pyArduinoML.methodchaining.TransitionBuilder import TransitionBuilder

class TransitionTableBuilder:
    """
    Builder for defining the transition table.
    """

    def __init__(self, parent, states: dict, sensors: dict):
        """
        Constructor for TransitionTableBuilder.

        :param parent: AppBuilder, the parent builder.
        :param states: dict, mapping of state names to State instances.
        :param sensors: dict, mapping of sensor names to Sensor instances.
        """
        self.parent = parent
        self.states = states
        self.sensors = sensors

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
