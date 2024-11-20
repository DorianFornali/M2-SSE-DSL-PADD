package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;

public class SignalTransition extends Transition {

    private ConditionTree condition;

    public ConditionTree getCondition() {
        return condition;
    }

    public void setCondition(ConditionTree condition) {
        this.condition = condition;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
