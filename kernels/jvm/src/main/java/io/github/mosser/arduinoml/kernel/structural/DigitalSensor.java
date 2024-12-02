package io.github.mosser.arduinoml.kernel.structural;

import io.github.mosser.arduinoml.kernel.generator.Visitor;

public class DigitalSensor extends Brick {
    
    public DigitalSensor(Integer pin) {
        PinAllocator.freeDigitalPin(pin);
        setPin(pin);
    }

    public DigitalSensor() {
        Integer pin = PinAllocator.getDigitalPin();
        setPin(pin);
    }

    @Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
