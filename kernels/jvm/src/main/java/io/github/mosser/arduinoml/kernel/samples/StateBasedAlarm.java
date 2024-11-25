package io.github.mosser.arduinoml.kernel.samples;

import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.Action;
import io.github.mosser.arduinoml.kernel.behavioral.Node;
import io.github.mosser.arduinoml.kernel.behavioral.SignalTransition;
import io.github.mosser.arduinoml.kernel.behavioral.State;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Actuator;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

import java.util.Arrays;

public class StateBasedAlarm {
    public static void main(String[] args) {

        // Declaring elementary bricks
        Sensor button = new Sensor();
        button.setName("button");
        button.setPin(9);

        Actuator led = new Actuator();
        led.setName("LED");
        led.setPin(12);

        // Declaring states
        State on = new State();
        on.setName("on");

        State off = new State();
        off.setName("off");

        Action switchTheLedOn = new Action();
        switchTheLedOn.setActuator(led);
        switchTheLedOn.setValue(SIGNAL.HIGH);

        Action switchTheLedOff = new Action();
        switchTheLedOff.setActuator(led);
        switchTheLedOff.setValue(SIGNAL.LOW);

        // Binding actions to states
        on.setActions(Arrays.asList(switchTheLedOn));
        off.setActions(Arrays.asList(switchTheLedOff));

        // Creating transitions
        SignalTransition on2off = new SignalTransition();
        on2off.setNext(off);
        Node on2offCondition = new Node();
        on2offCondition.setSensor(button);
        on2offCondition.setValue(SIGNAL.HIGH);
        on2off.setCondition(on2offCondition);

        SignalTransition off2on = new SignalTransition();
        off2on.setNext(on);
        Node off2onCondition = new Node();
        off2onCondition.setSensor(button);
        off2onCondition.setValue(SIGNAL.HIGH);
        off2on.setCondition(off2onCondition);

        // Binding transitions to states
        on.addTransition(on2off);
        off.addTransition(off2on);

        // Building the App
        App theAlarm = new App();
        theAlarm.setName("StateBasedAlarm!");
        theAlarm.setBricks(Arrays.asList(button, led));
        theAlarm.setStates(Arrays.asList(on, off));
        theAlarm.setInitial(off);

        // Generating Code
        Visitor codeGenerator = new ToWiring();
        theAlarm.accept(codeGenerator);

        // Printing the generated code on the console
        System.out.println(codeGenerator.getResult());
    }

}