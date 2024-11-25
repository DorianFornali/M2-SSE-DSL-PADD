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
	private State initialState;
	
	private Binding binding;
	
	public GroovuinoMLModel(Binding binding) {
		this.bricks = new ArrayList<Brick>();
		this.states = new ArrayList<State>();
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
		if (conditions.size() == 1) {
			Node node = new Node();
			Map<String, Object> condition = conditions.get(0);
			String sensorName = (String) condition.get("sensor");
			Sensor sensor = (Sensor) this.binding.getVariable(sensorName);
			SIGNAL value = (SIGNAL) condition.get("value");
			node.setSensor(sensor);
			node.setValue(value);
			return node;
		} else {
			NodeTree nodeTree = new NodeTree();
			nodeTree.setOperator(OPERATOR.valueOf((String) conditions.get(0).get("operator")));

			Node left = new Node();
			Map<String, Object> leftCondition = conditions.get(1);
			String leftSensorName = (String) leftCondition.get("sensor");
			Sensor leftSensor = (Sensor) this.binding.getVariable(leftSensorName);
			SIGNAL leftValue = (SIGNAL) leftCondition.get("value");
			left.setSensor(leftSensor);
			left.setValue(leftValue);

			Node right = new Node();
			Map<String, Object> rightCondition = conditions.get(2);
			String rightSensorName = (String) rightCondition.get("sensor");
			Sensor rightSensor = (Sensor) this.binding.getVariable(rightSensorName);
			SIGNAL rightValue = (SIGNAL) rightCondition.get("value");
			right.setSensor(rightSensor);
			right.setValue(rightValue);

			nodeTree.setLeftTree(left);
			nodeTree.setRightTree(right);

			return nodeTree;
		}
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
		Visitor codeGenerator = new ToWiring();
		app.accept(codeGenerator);
		
		return codeGenerator.getResult();
	}
}
