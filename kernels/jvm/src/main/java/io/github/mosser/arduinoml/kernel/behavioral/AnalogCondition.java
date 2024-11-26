package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.COMPARATOR;
import io.github.mosser.arduinoml.kernel.structural.Constant;

public class AnalogCondition extends Condition {
    
    private Constant value;
    private COMPARATOR comparator;

    public COMPARATOR getComparator() {
        return comparator;
    }

    public void setComparator(COMPARATOR comparator) {
        this.comparator = comparator;
    }

    public Constant getValue() {
        return value;
    }

    public void setValue(Constant value) {
        this.value = value;
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toPrettyString() {
        return value.toString() + " " + comparator;
    }
    
}
