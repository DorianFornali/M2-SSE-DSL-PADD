package io.github.mosser.arduinoml.kernel;

import io.github.mosser.arduinoml.kernel.behavioral.State;
import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Brick;
import io.github.mosser.arduinoml.kernel.structural.Constant;

import java.util.ArrayList;
import java.util.List;

public class App implements NamedElement, Visitable {

	private String name;
	private List<Brick> bricks = new ArrayList<Brick>();
	private List<State> states = new ArrayList<State>();
	private List<Constant> constants = new ArrayList<Constant>();
	private State initial;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public List<Brick> getBricks() {
		return bricks;
	}

	public void setBricks(List<Brick> bricks) {
		this.bricks = bricks;
	}

	public List<State> getStates() {
		return states;
	}

	public void setStates(List<State> states) {
		this.states = states;
	}

	public State getInitial() {
		return initial;
	}

	public void setInitial(State initial) {
		this.initial = initial;
	}

	public List<Constant> getConstants() {
		return constants;
	}

	public void setConstants(List<Constant> constants) {
		this.constants = constants;
	}

	public void addConstant(Constant constant) {
		if (this.constants == null) {
			this.constants = new ArrayList<Constant>();
		}
		this.constants.add(constant);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
