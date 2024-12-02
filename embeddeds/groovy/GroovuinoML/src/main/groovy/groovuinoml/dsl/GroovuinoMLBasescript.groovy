package groovuinoml.dsl

import io.github.mosser.arduinoml.kernel.behavioral.AnalogAction
import io.github.mosser.arduinoml.kernel.behavioral.DigitalAction
import io.github.mosser.arduinoml.kernel.behavioral.Action
import io.github.mosser.arduinoml.kernel.behavioral.State
import io.github.mosser.arduinoml.kernel.structural.AnalogActuator
import io.github.mosser.arduinoml.kernel.structural.Brick
import io.github.mosser.arduinoml.kernel.structural.Constant
import io.github.mosser.arduinoml.kernel.structural.DigitalActuator
import io.github.mosser.arduinoml.kernel.structural.SIGNAL

abstract class GroovuinoMLBasescript extends Script {

	private List<Constant> constants;

	// analogSensor "name" [pin n]
	def analogSensor(String name) {
		System.out.println("Creating analog sensor with name: ${name}")
		def createSensor = { n ->
			if (n != null) {
				((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createAnalogSensor(name, n)
			} else {
				((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createAnalogSensor(name)
			}
		}

		[pin: { n -> createSensor(n) },
		 onPin: { n -> createSensor(n) },
		 done: { val -> createSensor() }]
	}

	// digitalSensor "name" [pin n]
	def digitalSensor(String name) {
		def createSensor = { n ->
			if (n != null) {
				((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createDigitalSensor(name, n)
			} else {
				((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createDigitalSensor(name)
			}
		}

		[pin: { n -> createSensor(n) },
		 onPin: { n -> createSensor(n) },
		 done: { val -> createSensor() }]
	}

	// analogActuator "name" [pin n]
	def analogActuator(String name) {
		def createActuator = { n ->
			if (n != null) {
				((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createAnalogActuator(name, n)
			} else {
				((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createAnalogActuator(name)
			}
		}

		[pin: { n -> createActuator(n) },
		 onPin: { n -> createActuator(n) },
		 done: { val -> createActuator() }]
	}

	// digitalActuator "name" [pin n]
	def digitalActuator(String name) {
		def createActuator = { n = null ->
			if (n != null) {
				((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createDigitalActuator(name, n)
			} else {
				((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createDigitalActuator(name)
			}
		}

		[pin: { n -> createActuator(n) },
		 onPin: { n -> createActuator(n) },
		 done: { val -> createActuator() }]
	}

	// state "name" means actuator becomes signal [and actuator becomes signal]*n
	def state(String name) {
		List<Action> actions = new ArrayList<Action>()
		((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createState(name, actions)
		// recursive closure to allow multiple and statements
		def closure
		closure = { actuator -> 
			[becomes: { signal ->
				Action action = null;
				if (signal instanceof String) {
					action = new DigitalAction();
					action.setActuator(actuator instanceof String ? (DigitalActuator)((GroovuinoMLBinding)this.getBinding()).getVariable(actuator) : (DigitalActuator)actuator);
					action.setValue(signal instanceof String ? (SIGNAL)((GroovuinoMLBinding)this.getBinding()).getVariable(signal) : SIGNAL.valueOf(signal));
				} else {
					action = new AnalogAction();
					action.setActuator(actuator instanceof String ? (AnalogActuator)((GroovuinoMLBinding)this.getBinding()).getVariable(actuator) : (AnalogActuator)actuator);
					action.setValue(getConstant(signal));
				}
				actions.add(action)
				[and: closure]
			}]
		}
		[means: closure]
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
	
	// initial state
	def initial(state) {
		((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().setInitialState(state instanceof String ? (State)((GroovuinoMLBinding)this.getBinding()).getVariable(state) : (State)state)
	}

	// from state1 to state2 when sensor becomes signal [and/or sensor becomes signal]*n done
	def from(state1) {
		[to: { state2 ->
			def transitionBuilder = new TransitionBuilder(
					state1 instanceof String ? (State)((GroovuinoMLBinding)this.getBinding()).getVariable(state1) : (State)state1,
					state2 instanceof String ? (State)((GroovuinoMLBinding)this.getBinding()).getVariable(state2) : (State)state2
			)

			def addComparisonCondition = { sensor, value, operator ->
				transitionBuilder.addCondition(
						sensor instanceof String ? (Brick)((GroovuinoMLBinding)this.getBinding()).getVariable(sensor) : (Brick)sensor,
						value,
						operator
				)
				[and: { nextSensor -> closure(nextSensor, "AND") }, or: { nextSensor -> closure(nextSensor, "OR") },
				 done: { val ->
					 transitionBuilder.finalizeTransition(this.getBinding() as GroovuinoMLBinding)
					 println "Transition finalized with result: ${val}"
				 }]
			}

			def closure
			closure = { sensor, logicalOperator = null ->
				[becomes: { signal ->
					transitionBuilder.addCondition(
							sensor instanceof String ? (Brick)((GroovuinoMLBinding)this.getBinding()).getVariable(sensor) : (Brick)sensor,
							signal instanceof String ? (SIGNAL)((GroovuinoMLBinding)this.getBinding()).getVariable(signal) : (SIGNAL)signal,
							logicalOperator == null ? "EQ" : logicalOperator
					)
					[and: { nextSensor -> closure(nextSensor, "AND") }, or: { nextSensor -> closure(nextSensor, "OR") },
					 done: { val ->
						 transitionBuilder.finalizeTransition(this.getBinding() as GroovuinoMLBinding)
					 }]
				},
				 GT: { value -> addComparisonCondition(sensor, value, "GT") },
				 LEQ: { value -> addComparisonCondition(sensor, value, "LEQ") },
				 EQ: { value -> addComparisonCondition(sensor, value, "EQ") },
				 NEQ: { value -> addComparisonCondition(sensor, value, "NEQ") },
				 LT: { value -> addComparisonCondition(sensor, value, "LT") },
				 GEQ: { value -> addComparisonCondition(sensor, value, "GEQ") }]
			}

			[when: { sensor -> closure(sensor) }]
		}]
	}


	// export name
	def export(String name) {
		println(((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().generateCode(name).toString())
	}
	
	// disable run method while running
	int count = 0
	abstract void scriptBody()
	def run() {
		if(count == 0) {
			count++
			scriptBody()
		} else {
			println "Run method is disabled"
		}
	}
}

class TransitionBuilder {
	State fromState
	State toState
	List<Map<String, Object>> conditions = []

	TransitionBuilder(State fromState, State toState) {
		this.fromState = fromState
		this.toState = toState
	}

	void addCondition(Brick sensor, SIGNAL signal, String operator) {
		conditions.add([
				sensor: sensor,
				signal: signal,
				operator: operator,
				comparator: null
		])
	}

	void addCondition(Brick sensor, Number value,String comparator) {
		conditions.add([
				sensor: sensor,
				value: value,
				operator: null,
				comparator: comparator
		])
	}

	void finalizeTransition(GroovuinoMLBinding binding) {
		((GroovuinoMLBinding) binding).getGroovuinoMLModel().createTransition(fromState, toState, conditions)
	}
}