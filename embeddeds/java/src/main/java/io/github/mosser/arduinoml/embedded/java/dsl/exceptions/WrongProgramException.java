package io.github.mosser.arduinoml.embedded.java.dsl.exceptions;

public class WrongProgramException extends RuntimeException {
    public WrongProgramException(String message) {
        super(message);
    }
}
