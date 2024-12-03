from enum import Enum


class Comparator(Enum):
    """
    Enumeration of comparison operators.
    """
    EQ = "=="
    NEQ = "!="
    GT = ">"
    GEQ = ">="
    LT = "<"
    LEQ = "<="

    def __str__(self):
        """
        Returns the  string representation of the comparator.

        :return: str, the comparator as a string.
        """
        return self.value
