grammar Arduinoml;

/******************
 ** Parser rules **
 ******************/

root            :   declaration bricks states EOF;

declaration     :   'application' name=IDENTIFIER;

bricks          :   (analogSensor | digitalSensor | analogActuator | digitalActuator)+;
    analogSensor      :   'analogSensor' location;
    digitalSensor     :   'digitalSensor' location;
    analogActuator    :   'analogActuator' location;
    digitalActuator   :   'digitalActuator' location;
    location          :   id=IDENTIFIER (':' port=PORT_NUMBER)?;

states          :   state+;
    state              :   initial? name=IDENTIFIER '{' (digitalAction | analogAction | transition)* '}';
    digitalAction      :   receiver=IDENTIFIER '<=' value=SIGNAL;
    analogAction       :  receiver=IDENTIFIER '<=' value=(INTEGER | FLOAT);
    transition         :   '=>' next=IDENTIFIER '{' conditionTree '}';
    initial            :   '->';

/*****************
 ** Conditions **
 *****************/

conditionTree          :   (digitalCondition | analogCondition) (OPERATOR (digitalCondition | analogCondition))?;
digitalCondition       :   trigger=IDENTIFIER 'is' value=SIGNAL;
analogCondition        :   trigger=IDENTIFIER COMPARATOR value=(INTEGER | FLOAT);

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
