package io.github.mosser.arduinoml.kernel.structural;

import io.github.mosser.arduinoml.kernel.generator.Visitor;

public class AnalogActuator extends Brick {
    
    public AnalogActuator(Integer pin) {
        PinAllocator.freeAnalogPin(pin);
        setPin(pin);
    }

    public AnalogActuator() {
        Integer pin = PinAllocator.getAnalogPin();
        setPin(pin);
    }

    @Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
