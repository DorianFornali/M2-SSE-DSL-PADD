package io.github.mosser.arduinoml.kernel.samples;

import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.DigitalAction;
import io.github.mosser.arduinoml.kernel.behavioral.DigitalCondition;
import io.github.mosser.arduinoml.kernel.behavioral.SignalTransition;
import io.github.mosser.arduinoml.kernel.behavioral.State;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Actuator;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

import java.util.Arrays;

public class VerySimpleAlarm {
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
        State pressed = new State();
        pressed.setName("pressed");

        State unpressed = new State();
        unpressed.setName("unpressed");

        // Creating actions
        DigitalAction switchTheBuzzerOn = new DigitalAction();
        switchTheBuzzerOn.setActuator(buzzer);
        switchTheBuzzerOn.setValue(SIGNAL.HIGH);
        DigitalAction switchTheLedOn = new DigitalAction();
        switchTheLedOn.setActuator(led);
        switchTheLedOn.setValue(SIGNAL.HIGH);

        DigitalAction switchTheBuzzerOff = new DigitalAction();
        switchTheBuzzerOff.setActuator(buzzer);
        switchTheBuzzerOff.setValue(SIGNAL.LOW);
        DigitalAction switchTheLedOff = new DigitalAction();
        switchTheLedOff.setActuator(led);
        switchTheLedOff.setValue(SIGNAL.LOW);

        // Binding actions to states
        pressed.setActions(Arrays.asList(switchTheBuzzerOn, switchTheLedOn));
        unpressed.setActions(Arrays.asList(switchTheBuzzerOff, switchTheLedOff));

        // Creating transitions
        SignalTransition pressed2unpressed = new SignalTransition();
        pressed2unpressed.setNext(unpressed);
        DigitalCondition pressed2unpressedCondition = new DigitalCondition();
        pressed2unpressedCondition.setSensor(button);
        pressed2unpressedCondition.setValue(SIGNAL.LOW);
        pressed2unpressed.setCondition(pressed2unpressedCondition);

        SignalTransition unpressed2pressed = new SignalTransition();
        unpressed2pressed.setNext(pressed);
        DigitalCondition unpressed2pressedCondition = new DigitalCondition();
        unpressed2pressedCondition.setSensor(button);
        unpressed2pressedCondition.setValue(SIGNAL.HIGH);
        unpressed2pressed.setCondition(unpressed2pressedCondition);

        // Binding transitions to states
        pressed.addTransition(pressed2unpressed);
        unpressed.addTransition(unpressed2pressed);

        // Building the App
        App theAlarm = new App();
        theAlarm.setName("Alarm!");
        theAlarm.setBricks(Arrays.asList(button, led, buzzer));
        theAlarm.setStates(Arrays.asList(pressed, unpressed));
        theAlarm.setInitial(unpressed);

        // Generating Code
        Visitor codeGenerator = new ToWiring();
        theAlarm.accept(codeGenerator);

        // Printing the generated code on the console
        System.out.println(codeGenerator.getResult());
    }

}