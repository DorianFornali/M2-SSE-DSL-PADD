package io.github.mosser.arduinoml.embedded.java.dsl;

import io.github.mosser.arduinoml.embedded.java.dsl.exceptions.WrongProgramException;
import io.github.mosser.arduinoml.kernel.behavioral.AnalogCondition;
import io.github.mosser.arduinoml.kernel.behavioral.BooleanCondition;
import io.github.mosser.arduinoml.kernel.behavioral.ConditionTree;
import io.github.mosser.arduinoml.kernel.behavioral.DigitalCondition;
import io.github.mosser.arduinoml.kernel.structural.COMPARATOR;
import io.github.mosser.arduinoml.kernel.structural.Constant;
import io.github.mosser.arduinoml.kernel.structural.OPERATOR;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;

public class NodeTreeBuilder {
    TransitionBuilder parent;
    ConditionTree local;
    String subTree;

    public NodeTreeBuilder(TransitionBuilder parent, String subTree) {
        this.parent = parent;
        this.subTree = subTree;
    }

    public void parseConditionString() {
        // The goal of this function is to recursively create the ConditionTree
        // We will first seek for the highestLevel and/or operator
        // The "highest level" is the one that is the least nested in parenthesis
        // Meaning it is the operator that is not in a parenthesis (or in the least nested parenthesis)
        // Then we will create another NodeTreeBuilder for the left and one for the right part
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
            // We are in a terminal case
            terminalCase(tokens);
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
        NodeTreeBuilder leftNodeTreeBuilder = new NodeTreeBuilder(parent, leftSubString);
        NodeTreeBuilder rightNodeTreeBuilder = new NodeTreeBuilder(parent, rightSubString);

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

    /** Called whenever we encounter a node without AND/OR meaning
     * we are at the end of the recursion for that branch
     * <br>
     * Will build the node accordingly, based on whether it is an analog or digital one
     * <br> We also need to create a constant dynamically in case the user input was a float */
    public void terminalCase(String[] tokens){
        if(tokens[2].equals("HIGH") || tokens[2].equals("LOW")){
            // Meaning we are on a digital condition
            DigitalCondition node = new DigitalCondition();
            node.setSensor(parent.parent.findSensor(tokens[0]));
            node.setValue(tokens[2].equals("HIGH") ? SIGNAL.HIGH : SIGNAL.LOW);

            local = node;
            if(!tokens[1].equals("==") ) {
                // Error from the dev, we should have == if we are in a digital condition
                throw new WrongProgramException("Digital condition should have == ('equals') as comparator, not: "
                        + tokens[1]);
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

                try {
                    Float.parseFloat(tokens[2]);
                } catch (NumberFormatException e) {
                    // Error from the dev, either he entered a wrong variable name or a non float value
                    throw new WrongProgramException("Invalid value in analog condition, " + tokens[2] +
                            " is not a float nor a known constant");
                }

                Constant constantAlreadyPresent = parent.parent.valueAlreadyPresent(Float.parseFloat(tokens[2]));
                if(constantAlreadyPresent != null){
                    value = constantAlreadyPresent;
                }
                else {
                    value = new Constant(ConditionTreeBuilder.getNewNameForConstant(), Float.parseFloat(tokens[2]));
                    parent.parent.addConstant(value);
                }
            }

            AnalogCondition node = new AnalogCondition();
            node.setSensor(parent.parent.findSensor(tokens[0]));
            node.setValue(value);

            COMPARATOR comparator = translateComparator(tokens[1]);
            if(comparator == null) {
                throw new WrongProgramException("Invalid comparator in analog condition: " + tokens[1]);
            }
            node.setComparator(comparator);
            // Now we identify the comparator

            local = node;
        }


    }

    private COMPARATOR translateComparator(String token) {
        switch (token) {
            case "<":
                return COMPARATOR.LT;
            case "<=":
                return COMPARATOR.LEQ;
            case ">":
                return COMPARATOR.GT;
            case ">=":
                return COMPARATOR.GEQ;
            case "==":
                return COMPARATOR.EQ;
            case "!=":
                return COMPARATOR.NEQ;
            default:
                return null;

        }
    }


    private String removeOuterParentheses(String str) {
        while (str.startsWith("(") && str.endsWith(")")) {
            str = str.substring(1, str.length() - 1).trim();
        }
        return str;
    }

}
