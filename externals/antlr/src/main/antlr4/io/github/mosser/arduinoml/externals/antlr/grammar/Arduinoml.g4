grammar Arduinoml;

/******************
 ** Parser rules **
 ******************/

root            :   declaration bricks states EOF;

declaration     :   'application' name=IDENTIFIER;

bricks          :   (sensor | actuator)+;
    sensor      :   'sensor' location;
    actuator    :   'actuator' location;
    location    :   id=IDENTIFIER ':' port=PORT_NUMBER;

states          :   state+;
    state       :   initial? name=IDENTIFIER '{' (action | transition)* '}';
    action      :   receiver=IDENTIFIER '<=' value=SIGNAL;
    transition  :   '=>' next=IDENTIFIER '{' conditionTree '}';
    initial     :   '->';

/*****************
 ** Conditions **
 *****************/

conditionTree   :   (condition | analogCondition) (OPERATOR (condition | analogCondition))?;
condition       :   trigger=IDENTIFIER 'is' value=SIGNAL;
analogCondition :   trigger=IDENTIFIER COMPARATOR value=(INTEGER | FLOAT);

/*****************
 ** Lexer rules **
 *****************/

PORT_NUMBER     :   [1-9] | '10' | '11' | '12' | '13';
IDENTIFIER      :   LOWERCASE (LOWERCASE|UPPERCASE)+;
SIGNAL          :   'HIGH' | 'LOW';
OPERATOR        :   'AND' | 'OR';
COMPARATOR      :   'EQ' | 'NEQ' | 'GT' | 'GEQ' | 'LT' | 'LEQ';
INTEGER         :   [0-9]+;
FLOAT           :   [0-9]+ '.' [0-9]+;

/*************
 ** Helpers **
 *************/

fragment LOWERCASE  : [a-z];
fragment UPPERCASE  : [A-Z];
NEWLINE             : ('\r'? '\n' | '\r')+      -> skip;
WS                  : ((' ' | '\t')+)           -> skip;
COMMENT             : '#' ~( '\r' | '\n' )*     -> skip;
