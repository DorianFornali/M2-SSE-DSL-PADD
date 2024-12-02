package io.github.mosser.arduinoml.kernel.behavioral;

import java.util.ArrayList;
import java.util.List;

import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Brick;
import io.github.mosser.arduinoml.kernel.structural.DigitalSensor;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;

public class DigitalCondition extends ConditionTree {

    private SIGNAL value;
    private DigitalSensor sensor;

    public SIGNAL getValue() {
        return value;
    }

    public void setValue(SIGNAL value) {
        this.value = value;
    }

    public DigitalSensor getSensor() {
        return sensor;
    }

    public void setSensor(DigitalSensor sensor) {
        this.sensor = sensor;
    }

    @Override
    public List<Brick> getSensors() {
        List<Brick> sensors = new ArrayList<Brick>();
        sensors.add(sensor);
        return sensors;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toPrettyString() {
        return value.toString();
    }
    
}
