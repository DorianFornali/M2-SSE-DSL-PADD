package io.github.mosser.arduinoml.embedded.java.dsl;

import io.github.mosser.arduinoml.kernel.behavioral.Node;
import io.github.mosser.arduinoml.kernel.behavioral.NodeTree;
import io.github.mosser.arduinoml.kernel.structural.OPERATOR;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

public class NodeTreeBuilder {
    ConditionTreeBuilder parent;

    Sensor currentNodeSensor;
    String currentNodeOperator; // useless while no update in kernel
    SIGNAL currentNodeValue;

    NodeTree local = new NodeTree();

    public NodeTreeBuilder(ConditionTreeBuilder parent) {
        this.parent = parent;
    }

    public NodeTreeBuilder sensor(String sensorName) {
        currentNodeSensor = parent.parent.parent.findSensor(sensorName);
        return this;
    }

    public NodeTreeBuilder equals(){
        // Does nothing for now
        return this;
    }

    public NodeTreeBuilder value(SIGNAL value){
        currentNodeValue = value;
        return this;
    }

    public NodeTreeBuilder and(){
        Node node = new Node();
        node.setSensor(currentNodeSensor);
        node.setValue(currentNodeValue);
        // node.setOperator(currentNodeOperator);
        NodeTreeBuilder nodeTreeBuilder = new NodeTreeBuilder(parent);
        nodeTreeBuilder.local.setLeftTree(node);
        nodeTreeBuilder.local.setOperator(OPERATOR.AND);

        // We store in current right child whatever will be built next
        local.setRightTree(nodeTreeBuilder.local);
        return nodeTreeBuilder;
    }


    public NodeTreeBuilder or(){
        Node node = new Node();
        node.setSensor(currentNodeSensor);
        node.setValue(currentNodeValue);
        // node.setOperator(currentNodeOperator);
        NodeTreeBuilder nodeTreeBuilder = new NodeTreeBuilder(parent);
        nodeTreeBuilder.local.setLeftTree(node);
        nodeTreeBuilder.local.setOperator(OPERATOR.OR);

        // We store in current right child whatever will be built next
        local.setRightTree(nodeTreeBuilder.local);
        return nodeTreeBuilder;
    }

    public ConditionTreeBuilder endConditionTree(){
        Node node = new Node();
        node.setSensor(currentNodeSensor);
        node.setValue(currentNodeValue);
        local.setRightTree(node);
        return parent;
    }

}
