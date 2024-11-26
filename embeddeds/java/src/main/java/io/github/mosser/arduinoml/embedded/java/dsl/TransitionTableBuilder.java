package io.github.mosser.arduinoml.embedded.java.dsl;

import io.github.mosser.arduinoml.kernel.behavioral.State;
import io.github.mosser.arduinoml.kernel.behavioral.Transition;
import io.github.mosser.arduinoml.kernel.structural.Constant;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

import java.util.Map;

public class TransitionTableBuilder {

    private AppBuilder parent;
    private Map<String, State> states;
    private Map<String, Sensor> sensors;
    private Map<String, Constant> constants;

    TransitionTableBuilder(AppBuilder parent, Map<String, State> states, Map<String, Sensor> sensors, Map<String, Constant> constants) {
        this.parent = parent;
        this.states = states;
        this.sensors = sensors;
        this.constants = constants;
    }

    public TransitionBuilder from(String state) {
        TransitionBuilder builder = new TransitionBuilder(this, state);
        return builder;
    }


    public AppBuilder endTransitionTable() {
        return parent;
    }


    Sensor findSensor(String sensorName) {
        Sensor s = sensors.get(sensorName);
        if (s == null)
            throw new IllegalArgumentException("Unknown sensor: [" + sensorName + "]");
        return s;
    }

    State findState(String stateName) {
        State s = states.get(stateName);
        if (s == null)
            throw new IllegalArgumentException("Unknown state: ["+stateName+"]");
        return s;
    }

    Constant findConstant(String constantName) {
        Constant c = constants.get(constantName);
        if (c == null)
            throw new IllegalArgumentException("Unknown constant: ["+constantName+"]");
        return c;
    }

    boolean isConstant(String constantName) {
        if(constants == null) {
            return false;
        }
        return constants.containsKey(constantName);
    }

    /** For a given float, returns a Constant already created if there's one in the map */
    Constant valueAlreadyPresent(float value){
        for(Map.Entry<String, Constant> entry : constants.entrySet()) {
            if(entry.getValue().getValue() == value)
                return entry.getValue();
        }
        return null;
    }

    public void addConstant(Constant c){
        constants.put(c.getName(), c);
        // We must also put it in the app
        parent.theApp.getConstants().add(c);
    }


}
