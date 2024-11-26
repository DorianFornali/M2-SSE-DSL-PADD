package io.github.mosser.arduinoml.embedded.java.dsl;

public class ConditionTreeBuilder {
    TransitionBuilder parent;
    static int nGeneratedConstants = 0;


    /** Returns the name for an automatically generated constant (variable) */
    static String getNewNameForConstant(){
        return "AUTO_CONSTANT_" + nGeneratedConstants++;
    }

    /** A string representing the current condition being structured by the user
     * This string will be parsed once the user is done to dynamically create the corresponding
     * ConditionTree */
    private String currentCondition = "";

    public ConditionTreeBuilder(TransitionBuilder parent) {
        this.parent = parent;
    }

    public ConditionTreeBuilder sensor(String sensorName) {
        currentCondition += " " + sensorName + " ";
        return this;
    }

    public ConditionTreeBuilder openParenthesis(){
        currentCondition += " ( ";
        return this;
    }

    public ConditionTreeBuilder equals(String val){
        currentCondition += " == ";
        return this.value(val);
    }

    public ConditionTreeBuilder differentFrom(String val){
        currentCondition += " != ";
        return this.value(val);
    }

    public ConditionTreeBuilder greaterThan(String val){
        currentCondition += " > ";
        return this.value(val);
    }

    public ConditionTreeBuilder greaterOrEquals(String val){
        currentCondition += " >= ";
        return this.value(val);
    }

    public ConditionTreeBuilder lessThan(String val){
        currentCondition += " < ";
        return this.value(val);
    }

    public ConditionTreeBuilder lessOrEquals(String val){
        currentCondition += " <= ";
        return this.value(val);
    }

    private ConditionTreeBuilder value(String value){
        currentCondition += " " + value + " ";
        return this;
    }

    public ConditionTreeBuilder closeParenthesis(){
        currentCondition += " ) ";
        return this;
    }

    public ConditionTreeBuilder and(){
        currentCondition += " && ";
        return this;
    }

    public ConditionTreeBuilder or(){
        currentCondition += " || ";
        return this;
    }

    /** Will trigger the condition tree creation based on all operations described before */
    public TransitionBuilder endWhen(){
        NodeTreeBuilder nodeTreeBuilder = new NodeTreeBuilder(parent, currentCondition);
        nodeTreeBuilder.parseConditionString();
        parent.local.setCondition(nodeTreeBuilder.local);
        return parent;
    }



}
