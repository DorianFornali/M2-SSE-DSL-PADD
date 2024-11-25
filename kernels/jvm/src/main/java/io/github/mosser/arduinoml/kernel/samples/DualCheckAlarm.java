package io.github.mosser.arduinoml.kernel.samples;

import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.BooleanCondition;
import io.github.mosser.arduinoml.kernel.behavioral.DigitalAction;
import io.github.mosser.arduinoml.kernel.behavioral.DigitalCondition;
import io.github.mosser.arduinoml.kernel.behavioral.SignalTransition;
import io.github.mosser.arduinoml.kernel.behavioral.State;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Actuator;
import io.github.mosser.arduinoml.kernel.structural.OPERATOR;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

import java.util.Arrays;

public class DualCheckAlarm {
    public static void main(String[] args) {

        // Declaring elementary bricks
        Sensor button1 = new Sensor();
        button1.setName("button1");
        button1.setPin(9);

        Sensor button2 = new Sensor();
        button2.setName("button2");
        button2.setPin(10);

        Actuator buzzer = new Actuator();
        buzzer.setName("Buzzer");
        buzzer.setPin(13);

        // Declaring states
        State unpressed = new State();
        unpressed.setName("unpressed");

        State pressed = new State();
        pressed.setName("pressed");

        // Creating actions
        DigitalAction switchTheBuzzerOn = new DigitalAction();
        switchTheBuzzerOn.setActuator(buzzer);
        switchTheBuzzerOn.setValue(SIGNAL.HIGH);

        DigitalAction switchTheBuzzerOff = new DigitalAction();
        switchTheBuzzerOff.setActuator(buzzer);
        switchTheBuzzerOff.setValue(SIGNAL.LOW);

        // Binding actions to states
        unpressed.setActions(Arrays.asList(switchTheBuzzerOff));
        pressed.setActions(Arrays.asList(switchTheBuzzerOn));

        // Creating transitions
        SignalTransition unpressed2pressed = new SignalTransition();
        unpressed2pressed.setNext(pressed);
        BooleanCondition unpressed2pressedCondition = new BooleanCondition();
        unpressed2pressedCondition.setOperator(OPERATOR.AND);
        DigitalCondition nodeButt1 = new DigitalCondition();
        nodeButt1.setSensor(button1);
        nodeButt1.setValue(SIGNAL.HIGH);
        unpressed2pressedCondition.setLeftTree(nodeButt1);
        DigitalCondition nodeButt2 = new DigitalCondition();
        nodeButt2.setSensor(button2);
        nodeButt2.setValue(SIGNAL.HIGH);
        unpressed2pressedCondition.setRightTree(nodeButt2);
        unpressed2pressed.setCondition(unpressed2pressedCondition);

        SignalTransition pressed2unpressed = new SignalTransition();
        pressed2unpressed.setNext(unpressed);
        BooleanCondition pressed2unpressedCondition = new BooleanCondition();
        pressed2unpressedCondition.setOperator(OPERATOR.OR);
        DigitalCondition nodeButt1Unpressed = new DigitalCondition();
        nodeButt1Unpressed.setSensor(button1);
        nodeButt1Unpressed.setValue(SIGNAL.LOW);
        pressed2unpressedCondition.setLeftTree(nodeButt1Unpressed);
        DigitalCondition nodeButt2Unpressed = new DigitalCondition();
        nodeButt2Unpressed.setSensor(button2);
        nodeButt2Unpressed.setValue(SIGNAL.LOW);
        pressed2unpressedCondition.setRightTree(nodeButt2Unpressed);
        pressed2unpressed.setCondition(pressed2unpressedCondition);

        // Binding transitions to states
        pressed.addTransition(pressed2unpressed);
        unpressed.addTransition(unpressed2pressed);

        // Building the App
        App theAlarm = new App();
        theAlarm.setName("Alarm!");
        theAlarm.setBricks(Arrays.asList(buzzer, button1, button2));
        theAlarm.setStates(Arrays.asList(pressed, unpressed));
        theAlarm.setInitial(unpressed);

        // Generating Code
        Visitor codeGenerator = new ToWiring();
        theAlarm.accept(codeGenerator);

        // Printing the generated code on the console
        System.out.println(codeGenerator.getResult());
    }

}