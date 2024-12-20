package io.github.mosser.arduinoml.kernel.behavioral;

import java.util.List;

import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Brick;

public abstract class ConditionTree implements Visitable {

    public abstract List<Brick> getSensors();

    @Override
	public abstract void accept(Visitor visitor);

    public abstract String toPrettyString();
}
