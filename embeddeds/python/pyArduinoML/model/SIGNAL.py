from enum import Enum

class Signal(Enum):
    """
    Enumeration of signal values.
    """
    LOW = 0
    HIGH = 1

    def __str__(self):
        """
        Returns the string representation of the signal in Arduino language.
        """
        return self.name