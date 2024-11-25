package io.github.mosser.arduinoml.embedded.java.dsl;

import io.github.mosser.arduinoml.kernel.behavioral.Node;
import io.github.mosser.arduinoml.kernel.behavioral.NodeTree;
import io.github.mosser.arduinoml.kernel.structural.OPERATOR;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

public class ConditionTreeBuilder{
    TransitionBuilder parent;

    Sensor currentNodeSensor;
    String currentNodeOperator; // useless while no update in kernel
    SIGNAL currentNodeValue;

    public ConditionTreeBuilder(TransitionBuilder parent) {
        this.parent = parent;
    }

    public ConditionTreeBuilder sensor(String sensorName) {
        currentNodeSensor = parent.parent.findSensor(sensorName);
        return this;
    }

    public ConditionTreeBuilder equals(){
        // Does nothing for now
        return this;
    }

    public ConditionTreeBuilder value(SIGNAL value){
        currentNodeValue = value;
        return this;
    }

    /** If we get a and, that means we are in a complex condition with a tree building up, we delegate
     * to a NodeTreeBuilder*/
    public NodeTreeBuilder and(){
        Node node = new Node();
        node.setSensor(currentNodeSensor);
        node.setValue(currentNodeValue);
        // node.setOperator(currentNodeOperator);
        NodeTreeBuilder nodeTreeBuilder = new NodeTreeBuilder(this);
        nodeTreeBuilder.local.setLeftTree(node);
        nodeTreeBuilder.local.setOperator(OPERATOR.AND);
        parent.local.setCondition(nodeTreeBuilder.local);
        return nodeTreeBuilder;
    }

    /** If we get a and, that means we are in a complex condition with a tree building up, we delegate
     * to a NodeTreeBuilder*/
    public NodeTreeBuilder or(){
        Node node = new Node();
        node.setSensor(currentNodeSensor);
        node.setValue(currentNodeValue);
        // node.setOperator(currentNodeOperator);
        NodeTreeBuilder nodeTreeBuilder = new NodeTreeBuilder(this);
        nodeTreeBuilder.local.setLeftTree(node);
        nodeTreeBuilder.local.setOperator(OPERATOR.OR);
        parent.local.setCondition(nodeTreeBuilder.local);
        return nodeTreeBuilder;
    }

    /** In this case we build up the simple node, no tree there */
    public TransitionBuilder closeWhen(){
        Node node = new Node();
        node.setSensor(currentNodeSensor);
        node.setValue(currentNodeValue);
        // node.setOperator(currentNodeOperator);
        parent.local.setCondition(node);
        return parent;
    }


}
