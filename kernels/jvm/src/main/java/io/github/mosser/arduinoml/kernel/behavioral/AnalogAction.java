package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Constant;

public class AnalogAction extends Action {
    
    private Constant value;

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
    
}
