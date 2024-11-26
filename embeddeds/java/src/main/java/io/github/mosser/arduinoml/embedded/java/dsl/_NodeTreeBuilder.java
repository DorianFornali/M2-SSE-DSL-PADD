package io.github.mosser.arduinoml.embedded.java.dsl;

import io.github.mosser.arduinoml.kernel.behavioral.ConditionTree;
import io.github.mosser.arduinoml.kernel.behavioral.Node;
import io.github.mosser.arduinoml.kernel.behavioral.NodeTree;
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
        System.out.println("Parsing condition string: " + subTree);

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
            // Create a simple node
            Node simpleNode = new Node();
            simpleNode.setSensor(parent.parent.findSensor(tokens[0]));
            SIGNAL value = tokens[2].equals("HIGH") ? SIGNAL.HIGH : SIGNAL.LOW;
            simpleNode.setValue(value);
            local = simpleNode;
            //System.out.println("Simple node created with condition: " + subTree.trim());
            return;
        }
/*

        System.out.println("Tokens array: " + java.util.Arrays.toString(tokens));
        System.out.println("Highest level operator: " + tokens[highestLevelOperatorIndex] + " and is at index " + highestLevelOperatorIndex);
*/

        // Now we will build the left subString and the right subString
        String leftSubString = "";
        for (int i = 0; i < highestLevelOperatorIndex; i++) {
            leftSubString += tokens[i] + " ";
        }

        String rightSubString = "";
        for (int i = highestLevelOperatorIndex + 1; i < tokens.length; i++) {
            rightSubString += tokens[i] + " ";
        }

        /*System.out.println("Left subString: " + leftSubString);
        System.out.println("Right subString: " + rightSubString);*/

        // Now we will create the left and right NodeTreeBuilders
        _NodeTreeBuilder leftNodeTreeBuilder = new _NodeTreeBuilder(parent, leftSubString);
        _NodeTreeBuilder rightNodeTreeBuilder = new _NodeTreeBuilder(parent, rightSubString);

        // Now we will create the local ConditionTree
        local = new NodeTree();
        OPERATOR operator = tokens[highestLevelOperatorIndex].equals("&&") ? OPERATOR.AND : OPERATOR.OR;

        // We now delegate the parsing to the left and right NodeTreeBuilders
        System.out.println("Current condition tree for " + this.hashCode() + " " + local.toPrettyString());
        leftNodeTreeBuilder.parseConditionString();
        rightNodeTreeBuilder.parseConditionString();

        ((NodeTree) local).setOperator(operator);
        ((NodeTree) local).setLeftTree(leftNodeTreeBuilder.local);
        ((NodeTree) local).setRightTree(rightNodeTreeBuilder.local);
        System.out.println("End of parsing for " + this.hashCode() + " " + local.toPrettyString());
    }

}
