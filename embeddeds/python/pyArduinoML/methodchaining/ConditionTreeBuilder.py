from pyArduinoML.model.SIGNAL import Signal
class ConditionTreeBuilder:
    """
    Builder for constructing condition trees for transitions in an ArduinoML application.
    """

    n_generated_constants = 0  # Static variable for generating unique constant names

    @staticmethod
    def get_new_name_for_constant():
        """
        Generate a unique name for an automatically generated constant.

        :return: str, the generated constant name.
        """
        name = f"AUTO_CONSTANT_{ConditionTreeBuilder.n_generated_constants}"
        ConditionTreeBuilder.n_generated_constants += 1
        return name

    def __init__(self, parent):
        """ 
        Constructor for ConditionTreeBuilder.

        :param parent: TransitionBuilder, the parent builder for transitions.
        """
        self.parent = parent
        self.current_condition = ""  # String representing the condition being built
        self.sensor = None 
        self.logical_operator = None 

    def add_sensor(self, sensor_name):
        """
        Add a sensor to the condition.

        :param sensor_name: str, the name of the sensor.
        :return: self
        """
        self.sensor = self.parent.parent.find_sensor(sensor_name)
        if self.sensor is None:
            raise ValueError(f"Unknown sensor: [{sensor_name}]")
        self.parent.local.sensor = self.sensor
        self.current_condition += f" {sensor_name} "
        return self

    def isHigh(self):
        """
        Add a HIGH signal condition.

        :return: self
        """
        self.current_condition += "== HIGH"
        self.parent.local.value = Signal.HIGH
        return self

    def isLow(self):
        """
        Add a LOW signal condition.

        :return: self
        """
        self.current_condition += "== LOW"
        self.parent.local.value = Signal.LOW
        return self

    def open_parenthesis(self):
        """
        Add an opening parenthesis to the condition.

        :return: self
        """
        self.current_condition += " ( "
        return self

    def equals(self, value):
        """
        Add an equality condition.

        :param value: str, the value to compare.
        :return: self
        """
        self.current_condition += f" == {value}"
        return self

    def different_from(self, value):
        """
        Add a not-equal condition.

        :param value: str, the value to compare.
        :return: self
        """
        self.current_condition += f" != {value}"
        return self

    def greater_than(self, value):
        """
        Add a greater-than condition.

        :param value: str, the value to compare.
        :return: self
        """
        self.current_condition += f" > {value}"
        return self

    def greater_or_equals(self, value):
        """
        Add a greater-than-or-equal condition.

        :param value: str, the value to compare.
        :return: self
        """
        self.current_condition += f" >= {value}"
        return self

    def less_than(self, value):
        """
        Add a less-than condition.

        :param value: str, the value to compare.
        :return: self
        """
        self.current_condition += f" < {value}"
        return self

    def less_or_equals(self, value):
        """
        Add a less-than-or-equal condition.

        :param value: str, the value to compare.
        :return: self
        """
        self.current_condition += f" <= {value}"
        return self

    def value(self, value):
        """
        Add a value to the condition.

        :param value: str, the value to add.
        :return: self
        """
        self.current_condition += f" {value} "
        return self

    def close_parenthesis(self):
        """
        Add a closing parenthesis to the condition.

        :return: self
        """
        self.current_condition += " ) "
        return self

    def and_(self):
        """
        Add an AND logical operator.

        :return: self
        """
        self.current_condition += " && "
        self.logical_operator = "AND"
        return self

    def or_(self):
        """
        Add an OR logical operator.

        :return: self
        """
        self.current_condition += " || "
        self.logical_operator = "OR"
        return self

    def end_when(self):
        """
        Finalize the condition and build the corresponding ConditionTree.

        :return: TransitionBuilder, the parent transition builder.
        """
        from pyArduinoML.methodchaining.NodeTreeBuilder import NodeTreeBuilder

        node_tree_builder = NodeTreeBuilder(self.parent, self.current_condition)
        node_tree_builder.parse_condition_string()
        self.parent.local.condition = node_tree_builder.local
        return self.parent
