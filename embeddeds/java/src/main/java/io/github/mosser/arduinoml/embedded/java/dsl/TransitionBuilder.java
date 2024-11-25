package io.github.mosser.arduinoml.embedded.java.dsl;


import io.github.mosser.arduinoml.kernel.behavioral.SignalTransition;
import io.github.mosser.arduinoml.kernel.behavioral.Transition;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;

public class TransitionBuilder {


    TransitionTableBuilder parent;

    SignalTransition local;

    private ConditionTreeBuilder conditionTreeBuilder;

    TransitionBuilder(TransitionTableBuilder parent, String source) {
        this.parent = parent;
        this.local = new SignalTransition();
        parent.findState(source).addTransition(local);
    }


    public ConditionTreeBuilder when() {
        return new ConditionTreeBuilder(this);
    }

    public TransitionTableBuilder goTo(String state) {
        local.setNext(parent.findState(state));
        return parent;
    }


}
