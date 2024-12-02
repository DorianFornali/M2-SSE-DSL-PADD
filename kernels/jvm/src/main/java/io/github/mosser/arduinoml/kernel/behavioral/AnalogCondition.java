package io.github.mosser.arduinoml.kernel.behavioral;

import java.util.ArrayList;
import java.util.List;

import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.AnalogSensor;
import io.github.mosser.arduinoml.kernel.structural.Brick;
import io.github.mosser.arduinoml.kernel.structural.COMPARATOR;
import io.github.mosser.arduinoml.kernel.structural.Constant;

public class AnalogCondition extends ConditionTree {
    
    private Constant value;
    private COMPARATOR comparator;
    private AnalogSensor sensor;

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

    public AnalogSensor getSensor() {
        return sensor;
    }

    public void setSensor(AnalogSensor sensor) {
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
        return value.toString() + " " + comparator;
    }
    
}
