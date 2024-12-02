package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.DigitalActuator;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;

public class DigitalAction extends Action {
    
    private SIGNAL value;
    private DigitalActuator actuator;

    public SIGNAL getValue() {
        return value;
    }

    public void setValue(SIGNAL value) {
        this.value = value;
    }

    public DigitalActuator getActuator() {
        return actuator;
    }

    public void setActuator(DigitalActuator actuator) {
        this.actuator = actuator;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
