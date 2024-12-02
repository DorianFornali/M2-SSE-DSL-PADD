package io.github.mosser.arduinoml.kernel.structural;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class PinAllocator {
    
    private static final List<Integer> analogPins = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
    private static final List<Integer> digitalPins = new ArrayList<>(Arrays.asList(8, 9, 10, 11, 12));
    private static List<Integer> availableAnalogPins = new ArrayList<>(analogPins);
    private static List<Integer> availableDigitalPins = new ArrayList<>(digitalPins);

    public static Integer getAnalogPin() {
        if (availableAnalogPins.isEmpty()) {
            throw new RuntimeException("No more analog pins available");
        }
        return availableAnalogPins.remove(0);
    }

    public static Integer getDigitalPin() {
        if (availableDigitalPins.isEmpty()) {
            throw new RuntimeException("No more digital pins available");
        }
        return availableDigitalPins.remove(0);
    }

    public static void freeAnalogPin(Integer pin) {
        if (!analogPins.contains(pin)) {
            throw new RuntimeException("Pin " + pin + " is not an analog pin");
        }
        if (!availableAnalogPins.contains(pin)) {
            throw new RuntimeException("Pin " + pin + " is already attributed");
        }
        availableAnalogPins.remove(pin);
    }

    public static void freeDigitalPin(Integer pin) {
        if (!digitalPins.contains(pin)) {
            throw new RuntimeException("Pin " + pin + " is not a digital pin");
        }
        if (!availableDigitalPins.contains(pin)) {
            throw new RuntimeException("Pin " + pin + " is already attributed");
        }
        availableDigitalPins.remove(pin);
    }

}
