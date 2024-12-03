from pyArduinoML.model.BooleanCondition import BooleanCondition
from pyArduinoML.model.DigitalCondition import DigitalCondition
from pyArduinoML.model.OPERATOR import OPERATOR 
from pyArduinoML.model.SIGNAL import Signal
from pyArduinoML.methodchaining.ConditionTreeBuilder import ConditionTreeBuilder
from pyArduinoML.model.Constant import Constant
from pyArduinoML.model.AnalogCondition import AnalogCondition
from pyArduinoML.model.COMPARATOR import Comparator

class NodeTreeBuilder:
    """
    Builder for recursively creating a ConditionTree from a condition string.
    """

    def __init__(self, parent, sub_tree):
        """
        Initialize the NodeTreeBuilder.

        :param parent: TransitionBuilder, the parent transition builder.
        :param sub_tree: str, the condition string to parse.
        """
        self.parent = parent # Reference to the TransitionBuilder
        self.sub_tree = sub_tree # The condition string to parse
        self.local = None  # The ConditionTree built from this NodeTreeBuilder

    def parse_condition_string(self):
        """
        Recursively parse the condition string and construct the ConditionTree.
        """
        # Split the condition string into tokens
        tokens = self.sub_tree.split()
        tokens = [token for token in tokens if token]  # Remove empty tokens
        parenthesis_level = 0

        # Find the highest-level logical operator (AND/OR)
        highest_level_operator_index = -1
        highest_level_operator_parenthesis_level = float('inf')

        for i, token in enumerate(tokens):
            if token == "(":
                parenthesis_level += 1
            elif token == ")":
                parenthesis_level -= 1
            elif token in ["&&", "||"]:
                if parenthesis_level < highest_level_operator_parenthesis_level:
                    highest_level_operator_index = i
                    highest_level_operator_parenthesis_level = parenthesis_level

        # Check if no operator was found (terminal case)
        if highest_level_operator_index == -1:
            self.terminal_case(tokens)
            return

        # Split into left and right sub-strings
        left_sub_string = " ".join(tokens[:highest_level_operator_index])
        right_sub_string = " ".join(tokens[highest_level_operator_index + 1:])

        # Remove outer parentheses
        left_sub_string = self.remove_outer_parentheses(left_sub_string.strip())
        right_sub_string = self.remove_outer_parentheses(right_sub_string.strip())

        # Recursively create left and right NodeTreeBuilders
        left_node_tree_builder = NodeTreeBuilder(self.parent, left_sub_string)
        right_node_tree_builder = NodeTreeBuilder(self.parent, right_sub_string)

        # Parse the left and right sub-trees
        left_node_tree_builder.parse_condition_string()
        right_node_tree_builder.parse_condition_string()

        # Create a BooleanCondition for this node
        operator = OPERATOR.AND if tokens[highest_level_operator_index] == "&&" else OPERATOR.OR
        self.local = BooleanCondition(
            left_tree=left_node_tree_builder.local,
            right_tree=right_node_tree_builder.local,
            operator=operator
        )


    def terminal_case(self, tokens):
        """
        Handle the terminal case (leaf nodes of the ConditionTree).

        :param tokens: List[str], the tokens representing the terminal condition.
        """
        if tokens[2] in ["HIGH", "LOW"]:
            # DigitalCondition
            node = DigitalCondition()
            node.sensor = self.parent.parent.find_sensor(tokens[0])  # Use attribute assignment
            node.value = Signal.HIGH if tokens[2] == "HIGH" else Signal.LOW  # Use attribute assignment

            self.local = node
            if tokens[1] != "==":
                raise ValueError(f"Digital condition should have '==' as comparator, not: {tokens[1]}")
        else:
            # AnalogCondition
            is_constant = self.parent.parent.is_constant(tokens[2])
            if is_constant:
                value = self.parent.parent.find_constant(tokens[2])
            else:
                try:
                    float_value = float(tokens[2])
                except ValueError:
                    raise ValueError(f"Invalid value in analog condition: {tokens[2]} is not a float nor a known constant")

                value = self.parent.parent.value_already_present(float_value)
                if value is None:
                    value = Constant(ConditionTreeBuilder.get_new_name_for_constant(), float_value)
                    self.parent.parent.add_constant(value)

            node = AnalogCondition()
            node.sensor = self.parent.parent.find_sensor(tokens[0])  # Use attribute assignment
            node.value = value  # Use attribute assignment

            comparator = self.translate_comparator(tokens[1])
            if comparator is None:
                raise ValueError(f"Invalid comparator in analog condition: {tokens[1]}")
            node.comparator = comparator  # Use attribute assignment

            self.local = node


    def translate_comparator(self, token):
        """
        Translate a comparator string into a COMPARATOR enum.

        :param token: str, the comparator token.
        :return: COMPARATOR enum or None if invalid.
        """
        return {
            "<": Comparator.LT,
            "<=": Comparator.LEQ,
            ">": Comparator.GT,
            ">=": Comparator.GEQ,
            "==": Comparator.EQ,
            "!=": Comparator.NEQ
        }.get(token, None)

    def remove_outer_parentheses(self, string):
        """
        Remove outer parentheses from a string.

        :param string: str, the input string.
        :return: str, the string without outer parentheses.
        """
        while string.startswith("(") and string.endswith(")"):
            string = string[1:-1].strip()
        return string
