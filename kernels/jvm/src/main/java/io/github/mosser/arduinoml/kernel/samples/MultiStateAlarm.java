package io.github.mosser.arduinoml.kernel.samples;

import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.DigitalAction;
import io.github.mosser.arduinoml.kernel.behavioral.DigitalCondition;
import io.github.mosser.arduinoml.kernel.behavioral.SignalTransition;
import io.github.mosser.arduinoml.kernel.behavioral.State;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.DigitalActuator;
import io.github.mosser.arduinoml.kernel.structural.DigitalSensor;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;

import java.util.Arrays;

public class MultiStateAlarm {
    public static void main(String[] args) {

        // Declaring elementary bricks
        DigitalSensor button = new DigitalSensor();
        button.setName("button");

        DigitalActuator led = new DigitalActuator();
        led.setName("LED");

        DigitalActuator buzzer = new DigitalActuator();
        buzzer.setName("Buzzer");

        // Declaring states
        State initial = new State();
        initial.setName("initial");

        State buzzerOn = new State();
        buzzerOn.setName("buzzerOn");

        State ledOn = new State();
        ledOn.setName("ledOn");

        DigitalAction switchTheBuzzerOn = new DigitalAction();
        switchTheBuzzerOn.setActuator(buzzer);
        switchTheBuzzerOn.setValue(SIGNAL.HIGH);

        DigitalAction switchTheBuzzerOff = new DigitalAction();
        switchTheBuzzerOff.setActuator(buzzer);
        switchTheBuzzerOff.setValue(SIGNAL.LOW);

        DigitalAction switchTheLedOn = new DigitalAction();
        switchTheLedOn.setActuator(led);
        switchTheLedOn.setValue(SIGNAL.HIGH);

        DigitalAction switchTheLedOff = new DigitalAction();
        switchTheLedOff.setActuator(led);
        switchTheLedOff.setValue(SIGNAL.LOW);

        // Binding actions to states
        initial.setActions(Arrays.asList(switchTheLedOff, switchTheBuzzerOff));
        buzzerOn.setActions(Arrays.asList(switchTheBuzzerOn));
        ledOn.setActions(Arrays.asList(switchTheLedOn, switchTheBuzzerOff));

        // Creating transitions
        SignalTransition initial2buzzerOn = new SignalTransition();
        initial2buzzerOn.setNext(buzzerOn);
        DigitalCondition initial2BuzzerOnCondition = new DigitalCondition();
        initial2BuzzerOnCondition.setSensor(button);
        initial2BuzzerOnCondition.setValue(SIGNAL.HIGH);
        initial2buzzerOn.setCondition(initial2BuzzerOnCondition);

        SignalTransition buzzerOn2LedOn = new SignalTransition();
        buzzerOn2LedOn.setNext(ledOn);
        DigitalCondition buzzerOn2LedOnCondition = new DigitalCondition();
        buzzerOn2LedOnCondition.setSensor(button);
        buzzerOn2LedOnCondition.setValue(SIGNAL.HIGH);
        buzzerOn2LedOn.setCondition(buzzerOn2LedOnCondition);

        SignalTransition ledOn2Initial = new SignalTransition();
        ledOn2Initial.setNext(initial);
        DigitalCondition ledOn2InitialCondition = new DigitalCondition();
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