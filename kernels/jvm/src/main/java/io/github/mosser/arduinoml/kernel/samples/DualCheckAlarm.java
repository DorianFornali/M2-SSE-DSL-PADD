package io.github.mosser.arduinoml.kernel.samples;

import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.Action;
import io.github.mosser.arduinoml.kernel.behavioral.NodeTree;
import io.github.mosser.arduinoml.kernel.behavioral.SignalTransition;
import io.github.mosser.arduinoml.kernel.behavioral.State;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Actuator;
import io.github.mosser.arduinoml.kernel.structural.OPERATOR;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;
import io.github.mosser.arduinoml.kernel.behavioral.Node;

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
        Action switchTheBuzzerOn = new Action();
        switchTheBuzzerOn.setActuator(buzzer);
        switchTheBuzzerOn.setValue(SIGNAL.HIGH);

        Action switchTheBuzzerOff = new Action();
        switchTheBuzzerOff.setActuator(buzzer);
        switchTheBuzzerOff.setValue(SIGNAL.LOW);

        // Binding actions to states
        unpressed.setActions(Arrays.asList(switchTheBuzzerOff));
        pressed.setActions(Arrays.asList(switchTheBuzzerOn));

        // Creating transitions
        SignalTransition unpressed2pressed = new SignalTransition();
        unpressed2pressed.setNext(pressed);
        NodeTree unpressed2pressedCondition = new NodeTree();
        unpressed2pressedCondition.setOperator(OPERATOR.AND);
        Node nodeButt1 = new Node();
        nodeButt1.setSensor(button1);
        nodeButt1.setValue(SIGNAL.HIGH);
        unpressed2pressedCondition.setLeftTree(nodeButt1);
        Node nodeButt2 = new Node();
        nodeButt2.setSensor(button2);
        nodeButt2.setValue(SIGNAL.HIGH);
        unpressed2pressedCondition.setRightTree(nodeButt2);
        unpressed2pressed.setCondition(unpressed2pressedCondition);

        SignalTransition pressed2unpressed = new SignalTransition();
        pressed2unpressed.setNext(unpressed);
        NodeTree pressed2unpressedCondition = new NodeTree();
        pressed2unpressedCondition.setOperator(OPERATOR.OR);
        Node nodeButt1Unpressed = new Node();
        nodeButt1Unpressed.setSensor(button1);
        nodeButt1Unpressed.setValue(SIGNAL.LOW);
        pressed2unpressedCondition.setLeftTree(nodeButt1Unpressed);
        Node nodeButt2Unpressed = new Node();
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