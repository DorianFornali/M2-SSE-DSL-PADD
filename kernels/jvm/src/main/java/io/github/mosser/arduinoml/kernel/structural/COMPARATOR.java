package io.github.mosser.arduinoml.kernel.structural;

public enum COMPARATOR {
    EQ("=="),
    NEQ("!="),
    GT(">"),
    GEQ(">="),
    LT("<"),
    LEQ("<=");

    private String operator;

    COMPARATOR(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return operator;
    }
}

