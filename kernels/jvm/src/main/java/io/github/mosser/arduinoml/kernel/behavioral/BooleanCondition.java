package io.github.mosser.arduinoml.kernel.behavioral;

import java.util.ArrayList;
import java.util.List;

import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Brick;
import io.github.mosser.arduinoml.kernel.structural.OPERATOR;

public class BooleanCondition extends ConditionTree {

    private ConditionTree leftTree;
    private ConditionTree rightTree;

    private OPERATOR operator;

    public ConditionTree getLeftTree() {
        return leftTree;
    }

    public void setLeftTree(ConditionTree leftTree) {
        this.leftTree = leftTree;
    }

    public ConditionTree getRightTree() {
        return rightTree;
    }

    public void setRightTree(ConditionTree rightTree) {
        this.rightTree = rightTree;
    }

    public OPERATOR getOperator() {
        return operator;
    }

    public void setOperator(OPERATOR operator) {
        this.operator = operator;
    }

    @Override
    public List<Brick> getSensors() {
        List<Brick> sensors = new ArrayList<Brick>();
        sensors.addAll(leftTree.getSensors());
        sensors.addAll(rightTree.getSensors());
        return sensors;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toPrettyString() {
        return leftTree.toPrettyString() + " " + operator + " " + rightTree.toPrettyString();
    }
    
}
