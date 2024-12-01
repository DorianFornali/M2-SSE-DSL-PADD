from enum import Enum


class OPERATOR(Enum):
    """
    Enum representing logical operators AND and OR.
    """

    AND = "&&"
    OR = "||"

    def __str__(self):
        """
        Returns the string representation of the operator.

        :return: str
        """
        return self.value
