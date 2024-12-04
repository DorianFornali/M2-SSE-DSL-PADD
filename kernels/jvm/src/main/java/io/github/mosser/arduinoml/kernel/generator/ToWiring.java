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

		for (Brick brick: app.getBricks()){
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
	public void visit(AnalogActuator actuator) {
		if(context.get("pass") == PASS.ONE) {
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			w(String.format("  /////////////// CONNECT %s TO PIN A%s ///////////////\n", actuator.getName(), actuator.getPin()));
			w(String.format("  pinMode(%d, OUTPUT); // %s [Analog Actuator]\n", actuator.getPin(), actuator.getName()));
			return;
		}
	}

	@Override
	public void visit(DigitalActuator actuator) {
		if(context.get("pass") == PASS.ONE) {
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			w(String.format("  /////////////// CONNECT %s TO PIN D%s ///////////////\n", actuator.getName(), actuator.getPin()));
			w(String.format("  pinMode(%d, OUTPUT); // %s [Digital Actuator]\n", actuator.getPin(), actuator.getName()));
			return;
		}
	}

	@Override
	public void visit(AnalogSensor sensor) {
		if(context.get("pass") == PASS.ONE) {
			w(String.format("\nboolean %sBounceGuard = false;\n", sensor.getName()));
			w(String.format("long %sLastDebounceTime = 0;\n", sensor.getName()));
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			w(String.format("  /////////////// CONNECT %s TO PIN A%s ///////////////\n", sensor.getName(), sensor.getPin()));
			w(String.format("  pinMode(%d, INPUT);  // %s [Analog Sensor]\n", sensor.getPin(), sensor.getName()));
			return;
		}
	}

	@Override
	public void visit(DigitalSensor sensor) {
		if(context.get("pass") == PASS.ONE) {
			w(String.format("\nboolean %sBounceGuard = false;\n", sensor.getName()));
			w(String.format("long %sLastDebounceTime = 0;\n", sensor.getName()));
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			w(String.format("  /////////////// CONNECT %s TO PIN D%s ///////////////\n", sensor.getName(), sensor.getPin()));
			w(String.format("  pinMode(%d, INPUT);  // %s [Digital Sensor] CONNECT TO D%s\n", sensor.getPin(), sensor.getName(), sensor.getPin()));
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
			List<Brick> sensors = transition.getCondition().getSensors();
			List<Brick> uniqueSensors = new ArrayList<>();
			for (Brick sensor : sensors) {
				if (!uniqueSensors.contains(sensor)) {
					uniqueSensors.add(sensor);
				}
			}

			// Bounce guard
			for (Brick sensor : uniqueSensors) {
				String sensorName = sensor.getName();
				w(String.format("\t\t\t%sBounceGuard = millis() - %sLastDebounceTime > debounce;\n",
						sensorName, sensorName));
			}

			// Start if
			w(String.format("\t\t\tif("));

			for (Brick sensor : uniqueSensors) {
				String sensorName = sensor.getName();
				w(String.format(" %sBounceGuard &&", sensorName));
			}

			transition.getCondition().accept(this);

			// End if
			w(") {\n");
			
			for (Brick sensor : uniqueSensors) {
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
