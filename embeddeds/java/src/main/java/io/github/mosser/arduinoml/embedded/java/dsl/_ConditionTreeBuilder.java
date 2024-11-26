package io.github.mosser.arduinoml.embedded.java.dsl;

public class _ConditionTreeBuilder {
    TransitionBuilder parent;
    static int nGeneratedConstants = 0;
    /** Returns the name for an automatically generated constant (variable) */
    static String getNewNameForConstant(){
        return "AUTO_CONSTANT_" + nGeneratedConstants++;
    }

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

    public _ConditionTreeBuilder equals(String val){
        currentCondition += " == ";
        return this.value(val);
    }

    public _ConditionTreeBuilder differentFrom(String val){
        currentCondition += " != ";
        return this.value(val);
    }

    public _ConditionTreeBuilder greaterThan(String val){
        currentCondition += " > ";
        return this.value(val);
    }

    public _ConditionTreeBuilder greaterOrEquals(String val){
        currentCondition += " >= ";
        return this.value(val);
    }

    public _ConditionTreeBuilder lessThan(String val){
        currentCondition += " < ";
        return this.value(val);
    }

    public _ConditionTreeBuilder lessOrEquals(String val){
        currentCondition += " <= ";
        return this.value(val);
    }

    private _ConditionTreeBuilder value(String value){
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
        return parent;
    }



}
