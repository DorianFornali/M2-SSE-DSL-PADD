package io.github.mosser.arduinoml.kernel.samples;

import java.util.Arrays;

import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.AnalogCondition;
import io.github.mosser.arduinoml.kernel.behavioral.DigitalAction;
import io.github.mosser.arduinoml.kernel.behavioral.SignalTransition;
import io.github.mosser.arduinoml.kernel.behavioral.State;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.AnalogSensor;
import io.github.mosser.arduinoml.kernel.structural.COMPARATOR;
import io.github.mosser.arduinoml.kernel.structural.Constant;
import io.github.mosser.arduinoml.kernel.structural.DigitalActuator;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;

public class FireDetection {
    
    public static void main(String[] args) {
        // Declaring elementary bricks
        AnalogSensor temperature = new AnalogSensor();
        temperature.setName("temperature");

        DigitalActuator led = new DigitalActuator();
        led.setName("LED");

        // Declaring states
        State fireDetected = new State();
        fireDetected.setName("fireDetected");

        State noFireDetected = new State();
        noFireDetected.setName("noFireDetected");

        // Creating actions
        DigitalAction switchTheLedOn = new DigitalAction();
        switchTheLedOn.setActuator(led);
        switchTheLedOn.setValue(SIGNAL.HIGH);

        DigitalAction switchTheLedOff = new DigitalAction();
        switchTheLedOff.setActuator(led);
        switchTheLedOff.setValue(SIGNAL.LOW);

        // Binding actions to states
        fireDetected.setActions(Arrays.asList(switchTheLedOn));
        noFireDetected.setActions(Arrays.asList(switchTheLedOff));

        // Declaring constants
        Constant threshold = new Constant("threshold", 57.0);

        // Creating transitions
        SignalTransition fireDetected2noFireDetected = new SignalTransition();
        fireDetected2noFireDetected.setNext(noFireDetected);
        AnalogCondition temperatureHighCondition = new AnalogCondition();
        temperatureHighCondition.setSensor(temperature);
        temperatureHighCondition.setValue(threshold);
        temperatureHighCondition.setComparator(COMPARATOR.LEQ);
        fireDetected2noFireDetected.setCondition(temperatureHighCondition);

        SignalTransition noFireDetected2fireDetected = new SignalTransition();
        noFireDetected2fireDetected.setNext(fireDetected);
        AnalogCondition temperatureLowCondition = new AnalogCondition();
        temperatureLowCondition.setSensor(temperature);
        temperatureLowCondition.setValue(threshold);
        temperatureLowCondition.setComparator(COMPARATOR.GT);
        noFireDetected2fireDetected.setCondition(temperatureLowCondition);

        fireDetected.addTransition(fireDetected2noFireDetected);
        noFireDetected.addTransition(noFireDetected2fireDetected);

        // Building the App
        App theAlarm = new App();
        theAlarm.setName("FireDetection!");
        theAlarm.setBricks(Arrays.asList(temperature, led));
        theAlarm.setStates(Arrays.asList(fireDetected, noFireDetected));
        theAlarm.setInitial(noFireDetected);
        theAlarm.setConstants(Arrays.asList(threshold));

        // Generating Code
        Visitor codeGenerator = new ToWiring();
        theAlarm.accept(codeGenerator);

        // Printing the generated code on the console
        System.out.println(codeGenerator.getResult());
    }

}
