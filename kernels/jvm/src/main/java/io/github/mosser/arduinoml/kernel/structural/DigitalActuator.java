package io.github.mosser.arduinoml.kernel.structural;

import io.github.mosser.arduinoml.kernel.generator.Visitor;

public class DigitalActuator extends Brick {

    public DigitalActuator(Integer pin) {
        PinAllocator.freeDigitalPin(pin);
        setPin(pin);
    }

    public DigitalActuator() {
        Integer pin = PinAllocator.getDigitalPin();
        setPin(pin);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
    
}
