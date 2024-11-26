package groovuinoml.dsl

import io.github.mosser.arduinoml.kernel.behavioral.TimeUnit
import io.github.mosser.arduinoml.kernel.behavioral.Action
import io.github.mosser.arduinoml.kernel.behavioral.State
import io.github.mosser.arduinoml.kernel.structural.Actuator
import io.github.mosser.arduinoml.kernel.structural.Sensor
import io.github.mosser.arduinoml.kernel.structural.SIGNAL

abstract class GroovuinoMLBasescript extends Script {
//	public static Number getDuration(Number number, TimeUnit unit) throws IOException {
//		return number * unit.inMillis;
//	}

	// sensor "name" pin n
	def sensor(String name) {
		[pin: { n -> ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createSensor(name, n) },
		onPin: { n -> ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createSensor(name, n)}]
	}
	
	// actuator "name" pin n
	def actuator(String name) {
		[pin: { n -> ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createActuator(name, n) }]
	}
	
	// state "name" means actuator becomes signal [and actuator becomes signal]*n
	def state(String name) {
		List<Action> actions = new ArrayList<Action>()
		((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createState(name, actions)
		// recursive closure to allow multiple and statements
		def closure
		closure = { actuator -> 
			[becomes: { signal ->
				Action action = new Action()
				action.setActuator(actuator instanceof String ? (Actuator)((GroovuinoMLBinding)this.getBinding()).getVariable(actuator) : (Actuator)actuator)
				action.setValue(signal instanceof String ? (SIGNAL)((GroovuinoMLBinding)this.getBinding()).getVariable(signal) : (SIGNAL)signal)
				actions.add(action)
				[and: closure]
			}]
		}
		[means: closure]
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
			def closure
			closure = { sensor, operator = null ->
				[becomes: { signal ->
					transitionBuilder.addCondition(
							sensor instanceof String ? (Sensor)((GroovuinoMLBinding)this.getBinding()).getVariable(sensor) : (Sensor)sensor,
							signal instanceof String ? (SIGNAL)((GroovuinoMLBinding)this.getBinding()).getVariable(signal) : (SIGNAL)signal,
							operator
					)
					[and: { nextSensor -> closure(nextSensor, "AND") }, or: { nextSensor -> closure(nextSensor, "OR") },
					 done: { val ->
						 transitionBuilder.finalizeTransition(this.getBinding() as GroovuinoMLBinding)
					 }]
				}]
			}

			[when: { sensor -> closure(sensor)}]
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

	void addCondition(Sensor sensor, SIGNAL signal, String operator) {
		conditions.add([
				sensor: sensor,
				signal: signal,
				operator: operator
		])
	}

	void finalizeTransition(GroovuinoMLBinding binding) {
		((GroovuinoMLBinding) binding).getGroovuinoMLModel().createTransition(fromState, toState, conditions)
	}
}