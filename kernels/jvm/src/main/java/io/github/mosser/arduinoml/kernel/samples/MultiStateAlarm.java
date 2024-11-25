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

public class MultiStateAlarm {
    public static void main(String[] args) {

        // Declaring elementary bricks
        Sensor button = new Sensor();
        button.setName("button");
        button.setPin(9);

        Actuator led = new Actuator();
        led.setName("LED");
        led.setPin(12);

        Actuator buzzer = new Actuator();
        buzzer.setName("Buzzer");
        buzzer.setPin(13);

        // Declaring states
        State initial = new State();
        initial.setName("initial");

        State buzzerOn = new State();
        buzzerOn.setName("buzzerOn");

        State ledOn = new State();
        ledOn.setName("ledOn");

        Action switchTheBuzzerOn = new Action();
        switchTheBuzzerOn.setActuator(buzzer);
        switchTheBuzzerOn.setValue(SIGNAL.HIGH);

        Action switchTheBuzzerOff = new Action();
        switchTheBuzzerOff.setActuator(buzzer);
        switchTheBuzzerOff.setValue(SIGNAL.LOW);

        Action switchTheLedOn = new Action();
        switchTheLedOn.setActuator(led);
        switchTheLedOn.setValue(SIGNAL.HIGH);

        Action switchTheLedOff = new Action();
        switchTheLedOff.setActuator(led);
        switchTheLedOff.setValue(SIGNAL.LOW);

        // Binding actions to states
        initial.setActions(Arrays.asList(switchTheLedOff, switchTheBuzzerOff));
        buzzerOn.setActions(Arrays.asList(switchTheBuzzerOn));
        ledOn.setActions(Arrays.asList(switchTheLedOn, switchTheBuzzerOff));

        // Creating transitions
        SignalTransition initial2buzzerOn = new SignalTransition();
        initial2buzzerOn.setNext(buzzerOn);
        Node initial2BuzzerOnCondition = new Node();
        initial2BuzzerOnCondition.setSensor(button);
        initial2BuzzerOnCondition.setValue(SIGNAL.HIGH);
        initial2buzzerOn.setCondition(initial2BuzzerOnCondition);

        SignalTransition buzzerOn2LedOn = new SignalTransition();
        buzzerOn2LedOn.setNext(ledOn);
        Node buzzerOn2LedOnCondition = new Node();
        buzzerOn2LedOnCondition.setSensor(button);
        buzzerOn2LedOnCondition.setValue(SIGNAL.HIGH);
        buzzerOn2LedOn.setCondition(buzzerOn2LedOnCondition);

        SignalTransition ledOn2Initial = new SignalTransition();
        ledOn2Initial.setNext(initial);
        Node ledOn2InitialCondition = new Node();
        ledOn2InitialCondition.setSensor(button);
        ledOn2InitialCondition.setValue(SIGNAL.HIGH);
        ledOn2Initial.setCondition(ledOn2InitialCondition);

        // Binding transitions to states
        initial.addTransition(initial2buzzerOn);
        buzzerOn.addTransition(buzzerOn2LedOn);
        ledOn.addTransition(ledOn2Initial);

        // Building the App
        App theAlarm = new App();
        theAlarm.setName("MultiStateAlarm!");
        theAlarm.setBricks(Arrays.asList(button, led, buzzer));
        theAlarm.setStates(Arrays.asList(initial, buzzerOn, ledOn));
        theAlarm.setInitial(initial);

        // Generating Code
        Visitor codeGenerator = new ToWiring();
        theAlarm.accept(codeGenerator);

        // Printing the generated code on the console
        System.out.println(codeGenerator.getResult());
    }

}