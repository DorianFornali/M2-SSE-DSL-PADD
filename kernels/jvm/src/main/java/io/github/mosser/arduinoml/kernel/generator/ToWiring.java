package io.github.mosser.arduinoml.kernel.generator;

import java.util.ArrayList;
import java.util.List;

import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.*;
import io.github.mosser.arduinoml.kernel.structural.*;

/**
 * Quick and dirty visitor to support the generation of Wiring code
 */
public class ToWiring extends Visitor<StringBuffer> {
	enum PASS {ONE, TWO}

	public ToWiring() {
		this.result = new StringBuffer();
	}

	private void w(String s) {
		result.append(String.format("%s",s));
	}

	@Override
	public void visit(App app) {
		//first pass, create global vars
		context.put("pass", PASS.ONE);
		w("// Wiring code generated from an ArduinoML model\n");
		w(String.format("// Application name: %s\n", app.getName())+"\n");

		w("long debounce = 200;\n");
		w("\nenum STATE {");
		String sep ="";
		for(State state: app.getStates()){
			w(sep);
			state.accept(this);
			sep=", ";
		}
		w("};\n");
		if (app.getInitial() != null) {
			w("STATE currentState = " + app.getInitial().getName()+";\n");
		}

		if (app.getConstants().size() > 0)
			w("\n// constants\n");

		for (Constant constant : app.getConstants()) {
			w("const float " + constant.getName() + " = " + constant.getValue() + ";\n");
		}

		for(Brick brick: app.getBricks()){
			brick.accept(this);
		}

		//second pass, setup and loop
		context.put("pass",PASS.TWO);
		w("\nvoid setup(){\n");
		for(Brick brick: app.getBricks()){
			brick.accept(this);
		}
		w("}\n");

		w("\nvoid loop() {\n" +
			"\tswitch(currentState){\n");
		for(State state: app.getStates()){
			state.accept(this);
		}
		w("\t}\n" +
			"}");
	}

	@Override
	public void visit(Actuator actuator) {
		if(context.get("pass") == PASS.ONE) {
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			w(String.format("  pinMode(%d, OUTPUT); // %s [Actuator]\n", actuator.getPin(), actuator.getName()));
			return;
		}
	}


	@Override
	public void visit(Sensor sensor) {
		if(context.get("pass") == PASS.ONE) {
			w(String.format("\nboolean %sBounceGuard = false;\n", sensor.getName()));
			w(String.format("long %sLastDebounceTime = 0;\n", sensor.getName()));
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			w(String.format("  pinMode(%d, INPUT);  // %s [Sensor]\n", sensor.getPin(), sensor.getName()));
			return;
		}
	}

	@Override
	public void visit(State state) {
		if(context.get("pass") == PASS.ONE){
			w(state.getName());
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			w("\t\tcase " + state.getName() + ":\n");
			for (Action action : state.getActions()) {
				action.accept(this);
			}

			if (state.getTransitions() != null && !state.getTransitions().isEmpty()) {
				for(Transition transition: state.getTransitions()){
					transition.accept(this);
				}
				w("\t\tbreak;\n");
			}
			return;
		}

	}

	@Override
	public void visit(SignalTransition transition) {
		if(context.get("pass") == PASS.ONE) {
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			List<Sensor> sensors = transition.getCondition().getSensors();
			List<Sensor> uniqueSensors = new ArrayList<>();
			for (Sensor sensor : sensors) {
				if (!uniqueSensors.contains(sensor)) {
					uniqueSensors.add(sensor);
				}
			}

			// Bounce guard
			for (Sensor sensor : uniqueSensors) {
				String sensorName = sensor.getName();
				w(String.format("\t\t\t%sBounceGuard = millis() - %sLastDebounceTime > debounce;\n",
						sensorName, sensorName));
			}

			// Start if
			w(String.format("\t\t\tif("));

			for (Sensor sensor : uniqueSensors) {
				String sensorName = sensor.getName();
				w(String.format(" %sBounceGuard &&", sensorName));
			}

			transition.getCondition().accept(this);

			// End if
			w(") {\n");
			
			for (Sensor sensor : uniqueSensors) {
				String sensorName = sensor.getName();
				w(String.format("\t\t\t\t%sLastDebounceTime = millis();\n", sensorName));
			}
			w("\t\t\t\tcurrentState = " + transition.getNext().getName() + ";\n");
			w("\t\t\t}\n");

			return;
		}
	}

	@Override
	public void visit(TimeTransition transition) {
		if(context.get("pass") == PASS.ONE) {
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			int delayInMS = transition.getDelay();
			w(String.format("\t\t\tdelay(%d);\n", delayInMS));
			w("\t\t\t\tcurrentState = " + transition.getNext().getName() + ";\n");
			w("\t\t\t}\n");
			return;
		}
	}

	@Override
	public void visit(DigitalAction digitalAction) {
		if(context.get("pass") == PASS.ONE) {
			return;
		}

		if(context.get("pass") == PASS.TWO) {
			w(String.format("\t\t\tdigitalWrite(%d,%s);\n",digitalAction.getActuator().getPin(), digitalAction.getValue()));
			return;
		}
	}

	@Override
	public void visit(AnalogAction analogAction) {
		if(context.get("pass") == PASS.ONE) {
			return;
		}

		if(context.get("pass") == PASS.TWO) {
			Constant constant = analogAction.getValue();
			w(String.format("\t\t\tanalogWrite(%d,%s);\n",analogAction.getActuator().getPin(), constant.getName()));
			return;
		}
	}

	@Override
	public void visit(BooleanCondition booleanCondition) {
		w(" (");	
		booleanCondition.getLeftTree().accept(this);
		w(String.format("%s", booleanCondition.getOperator()));
		booleanCondition.getRightTree().accept(this);
		w(")");
	}

	@Override
	public void visit(DigitalCondition digitalCondition) {
		w(String.format(" digitalRead(%d) == %s ", digitalCondition.getSensor().getPin(), digitalCondition.getValue()));
	}

	@Override
	public void visit(AnalogCondition analogCondition) {
		w(String.format(" analogRead(%d) %s %s ", analogCondition.getSensor().getPin(), analogCondition.getComparator(), analogCondition.getValue().getName()));
	}

}
