package groovuinoml.dsl;

import java.util.*;

import groovy.lang.Binding;
import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.*;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.*;

public class GroovuinoMLModel {
	private List<Brick> bricks;
	private List<State> states;
	private List<Constant> constants;
	private State initialState;
	
	private Binding binding;
	
	public GroovuinoMLModel(Binding binding) {
		this.bricks = new ArrayList<Brick>();
		this.states = new ArrayList<State>();
		this.constants = new ArrayList<Constant>();
		this.binding = binding;
	}
	
	public void createSensor(String name, Integer pinNumber) {
		Sensor sensor = new Sensor();
		sensor.setName(name);
		sensor.setPin(pinNumber);
		this.bricks.add(sensor);
		this.binding.setVariable(name, sensor);
//		System.out.println("> sensor " + name + " on pin " + pinNumber);
	}
	
	public void createActuator(String name, Integer pinNumber) {
		Actuator actuator = new Actuator();
		actuator.setName(name);
		actuator.setPin(pinNumber);
		this.bricks.add(actuator);
		this.binding.setVariable(name, actuator);
	}
	
	public void createState(String name, List<Action> actions) {
		State state = new State();
		state.setName(name);
		state.setActions(actions);
		this.states.add(state);
		this.binding.setVariable(name, state);
	}

	public void createTransition(State from, State to, Sensor sensor, SIGNAL value) {
		SignalTransition transition = new SignalTransition();
		transition.setNext(to);
//		transition.setSensor(sensor);
//		transition.setValue(value);
//		from.setTransition(transition);
	}
	
	public void createTransition(State from, State to, List<Map<String, Object>> conditions) {
		SignalTransition transition = new SignalTransition();
		transition.setNext(to);
		transition.setCondition(parseCondition(conditions));
		from.addTransition(transition);
	}

	private ConditionTree parseCondition(List<Map<String, Object>> conditions) {
		if (conditions.size() == 1 && conditions.get(0).get("operator") != null) {
			return parseDigitalCondition((Map<String, Object>) conditions.get(0));
		} else if (conditions.size() == 1 && conditions.get(0).get("comparator") != null) {
			return parseAnalogCondition((Map<String, Object>) conditions.get(0));
		} else {
			BooleanCondition booleanCondition = new BooleanCondition();
			booleanCondition.setOperator(OPERATOR.valueOf((String) conditions.get(1).get("operator")));

			if (conditions.get(0).get("comparator") == null) {
				booleanCondition.setLeftTree(parseDigitalCondition((Map<String, Object>) conditions.get(0)));
			} else {
				booleanCondition.setLeftTree(parseAnalogCondition((Map<String, Object>) conditions.get(0)));
			}

			if (conditions.get(1).get("operator") != null) {
				booleanCondition.setRightTree(parseDigitalCondition((Map<String, Object>) conditions.get(1)));
			} else {
				booleanCondition.setRightTree(parseAnalogCondition((Map<String, Object>) conditions.get(1)));
			}

			return booleanCondition;
		}
	}

	private DigitalCondition parseDigitalCondition(Map<String, Object> condition) {
		DigitalCondition digitalCondition = new DigitalCondition();
		String sensorName = ((Sensor) condition.get("sensor")).getName();
		Sensor sensor = (Sensor) this.binding.getVariable(sensorName);
		SIGNAL value = (SIGNAL) condition.get("signal");
		digitalCondition.setSensor(sensor);
		digitalCondition.setValue(value);
		return digitalCondition;
	}

	private AnalogCondition parseAnalogCondition(Map<String, Object> condition) {
		AnalogCondition analogCondition = new AnalogCondition();
		String sensorName = ((Sensor) condition.get("sensor")).getName();
		Sensor sensor = (Sensor) this.binding.getVariable(sensorName);
		Constant constant = null;
		for (Constant c : this.constants) {
			if (c.getValue() == Double.parseDouble((String) condition.get("value").toString())) {
				constant = c;
				break;
			}
		}
		if (constant == null) {
			Random rand = new Random();
			constant = new Constant("AUTO_CONSTANT_" + rand.nextInt(1000), Double.parseDouble((String) condition.get("value").toString()));
			this.constants.add(constant);
		}
		analogCondition.setSensor(sensor);
		analogCondition.setComparator(COMPARATOR.valueOf((String) condition.get("comparator")));
		analogCondition.setValue(constant);
		return analogCondition;
	}


	// Implement this when we need Temporal transitions
//	public void createTransition(State from, State to, int delay) {
//		TimeTransition transition = new TimeTransition();
//		transition.setNext(to);
//		transition.setDelay(delay);
//		from.setTransition(transition);
//	}


	public void setInitialState(State state) {
		this.initialState = state;
	}
	
	@SuppressWarnings("rawtypes")
	public Object generateCode(String appName) {
		App app = new App();
		app.setName(appName);
		app.setBricks(this.bricks);
		app.setStates(this.states);
		app.setInitial(this.initialState);
		app.setConstants(this.constants);
		Visitor codeGenerator = new ToWiring();
		app.accept(codeGenerator);
		
		return codeGenerator.getResult();
	}
}
