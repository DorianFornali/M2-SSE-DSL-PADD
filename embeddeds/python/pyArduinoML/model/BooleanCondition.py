from typing import List
from pyArduinoML.model.ConditionTree import ConditionTree
from pyArduinoML.model.OPERATOR import OPERATOR
from pyArduinoML.model.Sensor import Sensor

class BooleanCondition(ConditionTree):

    """
    Represents a Boolean condition in a ConditionTree.

    It combines two sub-condition trees (left and right) using a logical operator (e.g., AND, OR).
    """

    def __init__(self, left_tree: ConditionTree, right_tree: ConditionTree, operator: OPERATOR):
        """
        Constructor for BooleanCondition.

        :param left_tree: ConditionTree, the left subtree of the condition
        :param right_tree: ConditionTree, the right subtree of the condition
        :param operator: OPERATOR, the logical operator (e.g., AND, OR)
        """
        self._left_tree = left_tree
        self._right_tree = right_tree
        self._operator = operator

    @property
    def left_tree(self) -> ConditionTree:
        """
        Getter for the left subtree.

        :return: ConditionTree
        """
        return self._left_tree

    @left_tree.setter
    def left_tree(self, left_tree: ConditionTree):
        """
        Setter for the left subtree.

        :param left_tree: ConditionTree
        """
        if not isinstance(left_tree, ConditionTree):
            raise TypeError("left_tree must be an instance of ConditionTree")
        self._left_tree = left_tree

    @property
    def right_tree(self) -> ConditionTree:
        """
        Getter for the right subtree.

        :return: ConditionTree
        """
        return self._right_tree

    @right_tree.setter
    def right_tree(self, right_tree: ConditionTree):
        """
        Setter for the right subtree.

        :param right_tree: ConditionTree
        """
        if not isinstance(right_tree, ConditionTree):
            raise TypeError("right_tree must be an instance of ConditionTree")
        self._right_tree = right_tree

    @property
    def operator(self) -> OPERATOR:
        """
        Getter for the logical operator.

        :return: OPERATOR
        """
        return self._operator

    @operator.setter
    def operator(self, operator: OPERATOR):
        """
        Setter for the logical operator.

        :param operator: OPERATOR
        """
        if not isinstance(operator, OPERATOR):
            raise TypeError("operator must be an instance of OPERATOR")
        self._operator = operator

    def getSensors(self) -> List[Sensor]:
        """
        Collects all sensors involved in the condition.

        :return: List[Sensor]
        """
        sensors = []
        if self._left_tree:
            sensors.extend(self._left_tree.getSensors())
        if self._right_tree:
            sensors.extend(self._right_tree.getSensors())
        return sensors
    
    def evaluate(self) -> bool:
        """
        Evaluates the Boolean condition.

        :return: bool, whether the condition is satisfied.
        """
        if not self._operator:
            raise ValueError("Operator must be specified for a BooleanCondition")

        if self._operator == OPERATOR.AND:
            left_result = self._left_tree.evaluate() if self._left_tree else True
            right_result = self._right_tree.evaluate() if self._right_tree else True
            return left_result and right_result
        elif self._operator == OPERATOR.OR:
            left_result = self._left_tree.evaluate() if self._left_tree else False
            right_result = self._right_tree.evaluate() if self._right_tree else False
            return left_result or right_result
        else:
            raise ValueError(f"Invalid operator: {self._operator}")

    def accept(self, visitor):
        self.left_tree.accept(visitor)
        visitor.visit_boolean_operator(self._operator)
        self.right_tree.accept(visitor)
    
