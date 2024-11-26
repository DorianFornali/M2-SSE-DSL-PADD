package io.github.mosser.arduinoml.embedded.java.dsl;

import io.github.mosser.arduinoml.kernel.behavioral.AnalogCondition;
import io.github.mosser.arduinoml.kernel.behavioral.BooleanCondition;
import io.github.mosser.arduinoml.kernel.behavioral.ConditionTree;
import io.github.mosser.arduinoml.kernel.behavioral.DigitalCondition;
import io.github.mosser.arduinoml.kernel.structural.COMPARATOR;
import io.github.mosser.arduinoml.kernel.structural.Constant;
import io.github.mosser.arduinoml.kernel.structural.OPERATOR;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;

public class _NodeTreeBuilder {
    TransitionBuilder parent;
    ConditionTree local;
    String subTree;

    public _NodeTreeBuilder(TransitionBuilder parent, String subTree) {
        this.parent = parent;
        this.subTree = subTree;
    }

    public void parseConditionString() {
        // The goal of this function is to recursively create the ConditionTree
        // We will first seek for the most atomic and/or operator
        // The "highest level" is the one that is the least nested in parenthesis
        // Meaning it is the operator that is not in a parenthesis (or in the least nested parenthesis)
        // Then we will create another NodeTreeBuilder for the left and one for the part
        // While setting the operator to the operator we found
        // And left child to the new left NodeTreeBuilder's local, same for the right one

        // First we split the string into tokens
        String[] tokens = subTree.split("\\s+");
        // Remove empty tokens
        tokens = java.util.Arrays.stream(tokens).filter(token -> !token.equals("")).toArray(String[]::new);
        int parenthesisLevel = 0;

        // We will iterate over the tokens to find the highest level operator
        // which depends on the least number of parenthesis
        int highestLevelOperatorIndex = -1;
        int highestLevelOperatorParenthesisLevel = Integer.MAX_VALUE;
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals("(")) {
                parenthesisLevel++;
            } else if (tokens[i].equals(")")) {
                parenthesisLevel--;
            } else if (tokens[i].equals("&&") || tokens[i].equals("||")) {
                if (parenthesisLevel < highestLevelOperatorParenthesisLevel) {
                    highestLevelOperatorIndex = i;
                    highestLevelOperatorParenthesisLevel = parenthesisLevel;
                }
            }
        }

        // Check if no operator was found
        if (highestLevelOperatorIndex == -1) {

            if(tokens[2].equals("HIGH") || tokens[2].equals("LOW")){
                // Meaning we are on a digital condition
                DigitalCondition node = new DigitalCondition();
                node.setSensor(parent.parent.findSensor(tokens[0]));
                node.setValue(tokens[2].equals("HIGH") ? SIGNAL.HIGH : SIGNAL.LOW);

                local = node;
                if(!tokens[1].equals("==")) {
                    // Error from the dev, we should have == if we are in a digital condition
                    //TODO! Throw a custom exception ?
                    System.out.println("Error: Digital condition should have == as comparator");

                }
            }

            else
            {
                // We are in an analog condition
                // Two possible cases, the value either is the name of a constant
                // Or a direct float value
                boolean isConstant = parent.parent.isConstant(tokens[2]);
                Constant value;

                if(isConstant){
                    value = parent.parent.findConstant(tokens[2]);
                }
                else {
                    // The user directly input a float value
                    // We first check if the entered value isnt already present in on of the constants
                    // If yes, we reuse it, if not we create a new one

                    // Is it actually a float ?
                    try {
                        Float.parseFloat(tokens[2]);
                    } catch (NumberFormatException e) {
                        // Error from the dev, either he entered a wrong variable name or a non float value
                        System.err.println("Error: Invalid value in analog condition, " + tokens[2] +
                                " is not a float nor a known constant");
                        System.exit(1);
                    }

                    Constant constantAlreadyPresent = parent.parent.valueAlreadyPresent(Float.parseFloat(tokens[2]));
                    if(constantAlreadyPresent != null){
                        value = constantAlreadyPresent;
                    }
                    else {
                        value = new Constant(_ConditionTreeBuilder.getNewNameForConstant(), Float.parseFloat(tokens[2]));
                        parent.parent.addConstant(value);
                    }
                }

                AnalogCondition node = new AnalogCondition();
                node.setSensor(parent.parent.findSensor(tokens[0]));
                node.setValue(value);

                // Now we identify the comparator
                switch(tokens[1]){
                    case "<":
                        node.setComparator(COMPARATOR.LT);
                        break;
                    case "<=":
                        node.setComparator(COMPARATOR.LEQ);
                        break;
                    case ">":
                        node.setComparator(COMPARATOR.GT);
                        break;
                    case ">=":
                        node.setComparator(COMPARATOR.GEQ);
                        break;
                    case "==":
                        node.setComparator(COMPARATOR.EQ);
                        break;
                    case "!=":
                        node.setComparator(COMPARATOR.NEQ);
                        break;
                    default:
                        // Error from the dev, we should have a valid comparator
                        //TODO! Throw a custom exception ?
                        System.out.println("Error: Invalid comparator in analog condition");
                }
                local = node;
            }

             return;
        }

        // Now we will build the left subString and the right subString
        String leftSubString = "";
        for (int i = 0; i < highestLevelOperatorIndex; i++) {
            leftSubString += tokens[i] + " ";
        }

        String rightSubString = "";
        for (int i = highestLevelOperatorIndex + 1; i < tokens.length; i++) {
            rightSubString += tokens[i] + " ";
        }

        // Remove outer parentheses from the left and right substrings
        leftSubString = removeOuterParentheses(leftSubString.trim());
        rightSubString = removeOuterParentheses(rightSubString.trim());

        // Now we will create the left and right NodeTreeBuilders
        _NodeTreeBuilder leftNodeTreeBuilder = new _NodeTreeBuilder(parent, leftSubString);
        _NodeTreeBuilder rightNodeTreeBuilder = new _NodeTreeBuilder(parent, rightSubString);

        // Now we will create the local ConditionTree
        local = new BooleanCondition();
        OPERATOR operator = tokens[highestLevelOperatorIndex].equals("&&") ? OPERATOR.AND : OPERATOR.OR;

        // We now delegate the parsing to the left and right NodeTreeBuilders
        leftNodeTreeBuilder.parseConditionString();
        rightNodeTreeBuilder.parseConditionString();

        ((BooleanCondition) local).setOperator(operator);
        ((BooleanCondition) local).setLeftTree(leftNodeTreeBuilder.local);
        ((BooleanCondition) local).setRightTree(rightNodeTreeBuilder.local);
    }

    private String removeOuterParentheses(String str) {
        while (str.startsWith("(") && str.endsWith(")")) {
            str = str.substring(1, str.length() - 1).trim();
        }
        return str;
    }

}
