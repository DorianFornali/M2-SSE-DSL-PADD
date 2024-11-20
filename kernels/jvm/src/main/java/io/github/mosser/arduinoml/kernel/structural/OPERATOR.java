package io.github.mosser.arduinoml.kernel.structural;

public enum OPERATOR {
    AND("&&"),
    OR("||");

    private String operator;

    OPERATOR(String operator) {
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
