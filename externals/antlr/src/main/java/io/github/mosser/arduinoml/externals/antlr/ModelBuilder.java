package io.github.mosser.arduinoml.externals.antlr;

import io.github.mosser.arduinoml.externals.antlr.grammar.*;


import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.*;
import io.github.mosser.arduinoml.kernel.structural.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ModelBuilder extends ArduinomlBaseListener {

    /********************
     ** Business Logic **
     ********************/

    private App theApp = null;
    private boolean built = false;

    public App retrieve() {
        if (built) { return theApp; }
        throw new RuntimeException("Cannot retrieve a model that was not created!");
    }

    /*******************
     ** Symbol tables **
     *******************/

    private Map<String, DigitalSensor>   digitalSensors   = new HashMap<>();
    private Map<String, AnalogSensor>   analogSensors    = new HashMap<>();

    private Map<String, DigitalActuator> digitalActuators = new HashMap<>();
    private Map<String, AnalogActuator>  analogActuators  = new HashMap<>();

    private Map<String, State>    states  = new HashMap<>();
    private Map<String, Binding>  bindings  = new HashMap<>();

    private class Binding { // used to support state resolution for transitions
        String to; // name of the next state, as its instance might not have been compiled yet
        ConditionTree condition;
    }

    private State currentState = null;

    /**************************
     ** Listening mechanisms **
     **************************/

    @Override
    public void enterRoot(ArduinomlParser.RootContext ctx) {
        built = false;
        theApp = new App();
    }

    @Override public void exitRoot(ArduinomlParser.RootContext ctx) {
        // Resolving states in transitions
        bindings.forEach((key, binding) ->  {
            SignalTransition t = new SignalTransition();
            t.setCondition(binding.condition);
            t.setNext(states.get(binding.to));
            states.get(key).addTransition(t);
        });
        this.built = true;
    }

    @Override
    public void enterDeclaration(ArduinomlParser.DeclarationContext ctx) {
        theApp.setName(ctx.name.getText());
    }

    @Override
    public void enterAnalogSensor(ArduinomlParser.AnalogSensorContext ctx) {
        AnalogSensor sensor = null;
        if (ctx.location().port != null) {
            sensor = new AnalogSensor(Integer.parseInt(ctx.location().port.getText()));
        } else {
            sensor = new AnalogSensor();
        }
        sensor.setName(ctx.location().id.getText());
        this.theApp.getBricks().add(sensor);
        analogSensors.put(sensor.getName(), sensor);
    }

    @Override
    public void enterDigitalSensor(ArduinomlParser.DigitalSensorContext ctx) {
        DigitalSensor sensor = null;
        if (ctx.location().port != null) {
            sensor = new DigitalSensor(Integer.parseInt(ctx.location().port.getText()));
        } else {
            sensor = new DigitalSensor();
        }
        sensor.setName(ctx.location().id.getText());
        this.theApp.getBricks().add(sensor);
        digitalSensors.put(sensor.getName(), sensor);
    }

    @Override
    public void enterAnalogActuator(ArduinomlParser.AnalogActuatorContext ctx) {
        AnalogActuator actuator = null;
        if (ctx.location().port != null) {
            actuator = new AnalogActuator(Integer.parseInt(ctx.location().port.getText()));
        } else {
            actuator = new AnalogActuator();
        }
        actuator.setName(ctx.location().id.getText());
        this.theApp.getBricks().add(actuator);
        analogActuators.put(actuator.getName(), actuator);
    }

    @Override
    public void enterDigitalActuator(ArduinomlParser.DigitalActuatorContext ctx) {
        DigitalActuator actuator = null;
        if (ctx.location().port != null) {
            actuator = new DigitalActuator(Integer.parseInt(ctx.location().port.getText()));
        } else {
            actuator = new DigitalActuator();
        }
        actuator.setName(ctx.location().id.getText());
        this.theApp.getBricks().add(actuator);
        digitalActuators.put(actuator.getName(), actuator);
    }

    @Override
    public void enterState(ArduinomlParser.StateContext ctx) {
        State local = new State();
        local.setName(ctx.name.getText());
        this.currentState = local;
        this.states.put(local.getName(), local);
    }

    @Override
    public void exitState(ArduinomlParser.StateContext ctx) {
        this.theApp.getStates().add(this.currentState);
        this.currentState = null;
    }

    @Override
    public void enterAnalogAction(ArduinomlParser.AnalogActionContext ctx) {
        AnalogAction action = new AnalogAction();
        action.setActuator(analogActuators.get(ctx.receiver.getText()));
        action.setValue(getConstant(Double.parseDouble(ctx.value.getText())));
        currentState.getActions().add(action);
    }

    @Override
    public void enterDigitalAction(ArduinomlParser.DigitalActionContext ctx) {
        DigitalAction action = new DigitalAction();
        action.setActuator(digitalActuators.get(ctx.receiver.getText()));
        action.setValue(SIGNAL.valueOf(ctx.value.getText()));
        currentState.getActions().add(action);
    }

    @Override
    public void enterTransition(ArduinomlParser.TransitionContext ctx) {
        Binding toBeResolvedLater = new Binding();
        toBeResolvedLater.to = ctx.next.getText();
        toBeResolvedLater.condition = parseCondition(ctx.conditionTree());
        bindings.put(currentState.getName(), toBeResolvedLater);
    }

    private ConditionTree parseCondition(ArduinomlParser.ConditionTreeContext ctx) {
        if (ctx.OPERATOR() == null && ctx.digitalCondition(0) != null) {
            return parseDigitalCondition(ctx.digitalCondition(0));
        } else if (ctx.OPERATOR() == null && ctx.analogCondition(0) != null) {
            return parseAnalogCondition(ctx.analogCondition(0));
        } else {
            BooleanCondition booleanCondition = new BooleanCondition();
            booleanCondition.setOperator(OPERATOR.valueOf(ctx.OPERATOR().getText()));

            if (ctx.digitalCondition(0) != null) {
                booleanCondition.setLeftTree(parseDigitalCondition(ctx.digitalCondition(0)));
            } else {
                booleanCondition.setLeftTree(parseAnalogCondition(ctx.analogCondition(0)));
            }

            if (ctx.digitalCondition(1) != null) {
                booleanCondition.setRightTree(parseDigitalCondition(ctx.digitalCondition(1)));
            } else {
                booleanCondition.setRightTree(parseAnalogCondition(ctx.analogCondition(1)));
            }

            return booleanCondition;
        }
    }

    private AnalogCondition parseAnalogCondition(ArduinomlParser.AnalogConditionContext ctx) {
        AnalogCondition condition = new AnalogCondition();
        condition.setSensor(analogSensors.get(ctx.trigger.getText()));
        Constant constant = null;
        for (Constant c : theApp.getConstants()) {
            if (c.getValue() == Double.parseDouble(ctx.value.getText())) {
                constant = c;
                break;
            }
        }
        if (constant == null) {
            Random rand = new Random();
            constant = new Constant("AUTO_CONSTANT_" + rand.nextInt(1000), Double.parseDouble(ctx.value.getText()));
            theApp.addConstant(constant);
        }
        condition.setValue(constant);
        condition.setComparator(COMPARATOR.valueOf(ctx.COMPARATOR().getText()));
        return condition;
    }

    private DigitalCondition parseDigitalCondition(ArduinomlParser.DigitalConditionContext ctx) {
        DigitalCondition condition = new DigitalCondition();
        condition.setSensor(digitalSensors.get(ctx.trigger.getText()));
        condition.setValue(SIGNAL.valueOf(ctx.value.getText()));
        return condition;
    }

    private Constant getConstant(Double value) {
        Constant constant = null;
        for (Constant c : theApp.getConstants()) {
            if (c.getValue() == Double.parseDouble(value.toString())) {
                constant = c;
                break;
            }
        }
        if (constant == null) {
            Random rand = new Random();
            constant = new Constant("AUTO_CONSTANT_" + rand.nextInt(1000), Double.parseDouble(value.toString()));
            theApp.addConstant(constant);
        }
        return constant;
    }


    @Override
    public void enterInitial(ArduinomlParser.InitialContext ctx) {
        this.theApp.setInitial(this.currentState);
    }
}

