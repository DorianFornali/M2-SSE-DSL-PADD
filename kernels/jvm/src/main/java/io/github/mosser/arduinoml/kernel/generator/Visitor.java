package io.github.mosser.arduinoml.kernel.generator;

import io.github.mosser.arduinoml.kernel.behavioral.*;
import io.github.mosser.arduinoml.kernel.structural.*;
import io.github.mosser.arduinoml.kernel.App;

import java.util.HashMap;
import java.util.Map;

public abstract class Visitor<T> {

	public abstract void visit(App app);

	public abstract void visit(State state);
	public abstract void visit(SignalTransition transition);
	public abstract void visit(TimeTransition transition);
	public abstract void visit(DigitalAction digitalAction);
	public abstract void visit(AnalogAction analogAction);
	public abstract void visit(BooleanCondition booleanCondition);
	public abstract void visit(DigitalCondition digitalCondition);
	public abstract void visit(AnalogCondition analogCondition);

	public abstract void visit(AnalogActuator analogActuator);
	public abstract void visit(DigitalActuator digitalActuator);
	public abstract void visit(AnalogSensor analogSensor);
	public abstract void visit(DigitalSensor digitalSensor);

	/***********************
	 ** Helper mechanisms **
	 ***********************/

	protected Map<String,Object> context = new HashMap<>();

	protected T result;

	public T getResult() {
		return result;
	}

}

