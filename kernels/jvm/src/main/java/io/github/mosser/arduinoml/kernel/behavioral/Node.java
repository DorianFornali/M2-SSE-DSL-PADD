package io.github.mosser.arduinoml.kernel.behavioral;

import java.util.List;
import java.util.ArrayList;

import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

public class Node extends ConditionTree {
    
    private SIGNAL value;
    private Sensor sensor;
    
    public SIGNAL getValue() {
        return value;
    }

    public void setValue(SIGNAL value) {
        this.value = value;
    }
    
    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    @Override
    public List<Sensor> getSensors() {
        List<Sensor> sensors = new ArrayList<Sensor>();
        sensors.add(sensor);
        return sensors;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toPrettyString() {
        return String.format("%s == %s", sensor.getName(), value);
    }

}
