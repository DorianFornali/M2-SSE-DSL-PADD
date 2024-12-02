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

	public void createDigitalSensor(String name) {
		DigitalSensor sensor = new DigitalSensor();
		createSensor(sensor, name);
	}

	public void createDigitalSensor(String name, Integer pinNumber) {
		DigitalSensor sensor = new DigitalSensor(pinNumber);
		createSensor(sensor, name);
	}

	public void createAnalogSensor(String name) {
		AnalogSensor sensor = new AnalogSensor();
		createSensor(sensor, name);
	}

	public void createAnalogSensor(String name, Integer pinNumber) {
		AnalogSensor sensor = new AnalogSensor(pinNumber);
		createSensor(sensor, name);
	}

	public void createSensor(Brick sensor, String name) {
		sensor.setName(name);
		this.bricks.add(sensor);
		this.binding.setVariable(name, sensor);
	}

	public void createDigitalActuator(String name) {
		DigitalActuator actuator = new DigitalActuator();
		createActuator(actuator, name);
	}

	public void createDigitalActuator(String name, Integer pinNumber) {
		DigitalActuator actuator = new DigitalActuator(pinNumber);
		createActuator(actuator, name);
	}

	public void createAnalogActuator(String name) {
		AnalogActuator actuator = new AnalogActuator();
		createActuator(actuator, name);
	}

	public void createAnalogActuator(String name, Integer pinNumber) {
		AnalogActuator actuator = new AnalogActuator(pinNumber);
		createActuator(actuator, name);
	}

	public void createActuator(Brick actuator, String name) {
		actuator.setName(name);
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
		String sensorName = ((DigitalSensor) condition.get("sensor")).getName();
		DigitalSensor sensor = (DigitalSensor) this.binding.getVariable(sensorName);
		SIGNAL value = (SIGNAL) condition.get("signal");
		digitalCondition.setSensor(sensor);
		digitalCondition.setValue(value);
		return digitalCondition;
	}

	private AnalogCondition parseAnalogCondition(Map<String, Object> condition) {
		AnalogCondition analogCondition = new AnalogCondition();
		String sensorName = ((AnalogSensor) condition.get("sensor")).getName();
		AnalogSensor sensor = (AnalogSensor) this.binding.getVariable(sensorName);
		analogCondition.setSensor(sensor);
		analogCondition.setComparator(COMPARATOR.valueOf((String) condition.get("comparator")));
		analogCondition.setValue(getConstant(Double.parseDouble((String) condition.get("value").toString())));
		return analogCondition;
	}

	private Constant getConstant(Double value) {
		Constant constant = null;
		for (Constant c : this.constants) {
			if (c.getValue() == value) {
				constant = c;
				break;
			}
		}
		if (constant == null) {
			Random rand = new Random();
			constant = new Constant("AUTO_CONSTANT_" + rand.nextInt(1000), value);
			this.constants.add(constant);
		}
		return constant;
	}

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
