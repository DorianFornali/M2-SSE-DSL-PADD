package io.github.mosser.arduinoml.embedded.java.dsl;

import io.github.mosser.arduinoml.kernel.behavioral.NodeTree;

public class _ConditionTreeBuilder {
    TransitionBuilder parent;

    private String currentCondition = "";

    public _ConditionTreeBuilder(TransitionBuilder parent) {
        this.parent = parent;
    }

    public _ConditionTreeBuilder sensor(String sensorName) {
        currentCondition += " " + sensorName + " ";
        return this;
    }

    public _ConditionTreeBuilder openParenthesis(){
        currentCondition += " ( ";
        return this;
    }

    public _ConditionTreeBuilder equals(){
        currentCondition += " == ";
        return this;
    }

    public _ConditionTreeBuilder value(String value){
        currentCondition += " " + value + " ";
        return this;
    }

    public _ConditionTreeBuilder closeParenthesis(){
        currentCondition += " ) ";
        return this;
    }

    public _ConditionTreeBuilder and(){
        currentCondition += " && ";
        return this;
    }

    public _ConditionTreeBuilder or(){
        currentCondition += " || ";
        return this;
    }

    /** Will trigger the condition tree creation based on all operations described before */
    public TransitionBuilder endWhen(){
        _NodeTreeBuilder nodeTreeBuilder = new _NodeTreeBuilder(parent, currentCondition);
        nodeTreeBuilder.parseConditionString();
        parent.local.setCondition(nodeTreeBuilder.local);
        System.out.println("End condition: " + nodeTreeBuilder.local.toPrettyString());
        return parent;
    }



}
