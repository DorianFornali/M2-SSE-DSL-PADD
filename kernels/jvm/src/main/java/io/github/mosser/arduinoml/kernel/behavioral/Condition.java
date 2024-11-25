package io.github.mosser.arduinoml.kernel.behavioral;

import java.util.List;
import java.util.ArrayList;

import io.github.mosser.arduinoml.kernel.structural.Sensor;

public abstract class Condition extends ConditionTree {
    
    private Sensor sensor;
    
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

}
