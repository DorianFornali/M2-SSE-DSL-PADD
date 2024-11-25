package io.github.mosser.arduinoml.kernel.structural;

import io.github.mosser.arduinoml.kernel.NamedElement;

public class Constant implements NamedElement {
    
    private String name;
    private double value;

    public Constant(String name, double value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
}
