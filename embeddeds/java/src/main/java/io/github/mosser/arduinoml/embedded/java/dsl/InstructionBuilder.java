package io.github.mosser.arduinoml.embedded.java.dsl;


import io.github.mosser.arduinoml.kernel.behavioral.Action;
import io.github.mosser.arduinoml.kernel.behavioral.AnalogAction;
import io.github.mosser.arduinoml.kernel.behavioral.DigitalAction;
import io.github.mosser.arduinoml.kernel.behavioral.DigitalCondition;
import io.github.mosser.arduinoml.kernel.structural.Actuator;
import io.github.mosser.arduinoml.kernel.structural.Constant;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;

import java.util.Optional;

public class InstructionBuilder {

    private StateBuilder parent;

    private DigitalAction localDigital = new DigitalAction();
    private AnalogAction localAnalog = new AnalogAction();

    InstructionBuilder(StateBuilder parent, String target) {
        this.parent = parent;
        Optional<Actuator> opt = parent.parent.findActuator(target);
        Actuator act = opt.orElseThrow(() -> new IllegalArgumentException("Illegal actuator: ["+target+"]"));
        localDigital.setActuator(act);
        localAnalog.setActuator(act);
    }

    public StateBuilder toHigh() { localDigital.setValue(SIGNAL.HIGH); return goUpDigital(); }

    public StateBuilder toLow() { localDigital.setValue(SIGNAL.LOW); return goUpDigital(); }

    public StateBuilder toValue(float value){
        localAnalog.setValue(new Constant("constant", value)); // TODO! Might change with constant modifications in kernel
        return goUpAnalog();
    }

    private StateBuilder goUpDigital() {
        parent.local.getActions().add(this.localDigital);
        return parent;
    }

    private StateBuilder goUpAnalog() {
        parent.local.getActions().add(this.localAnalog);
        return parent;
    }

}
