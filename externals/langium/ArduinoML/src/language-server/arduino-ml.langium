grammar ArduinoMl

entry App:
	'app' name=ID 'initial' 'state' initial=[State:ID]
	'{'
		'bricks'
		bricks+=Brick
		(bricks+=Brick)* 
		
		'states'
		states+=State
		( states+=State)* 		
	'}';

Brick :
	(Actuator | Sensor);

Actuator:
	{infer Actuator}
	'Actuator' name=ID ':' outputPin=INT;

Sensor :
	{infer Sensor}
	'Sensor'  name=ID ':' inputPin=INT;
	
State :
	name=ID '{'
		actions+=Action
		(actions+=Action)* 
		transition=Transition
	'}';


Action :
	actuator=[Actuator:ID] '<=' value=Signal;

Transition:
    '=>' next=[State:ID] '{' conditionTree=ConditionTree '}';

ConditionTree:
    root=(DigitalCondition | AnalogCondition) (operator=Operator right=(DigitalCondition | AnalogCondition))?;

DigitalCondition:
    trigger=[Sensor:ID] 'is' value=Signal;

AnalogCondition:
		trigger=[Sensor:ID] comparator=Comparator value=(FLOAT | INT);

Signal:
    value=(HIGH | LOW);
terminal HIGH : 'HIGH';
terminal LOW : 'LOW';

Operator:
		value=(AND | OR);
terminal AND : 'AND';
terminal OR : 'OR';

Comparator:
		value=(EQ | NEQ | LTE | LT | GTE | GT);
terminal EQ : 'EQ';
terminal NEQ : 'NEQ';
terminal LTE : 'LTE';
terminal LT : 'LT';
terminal GTE : 'GTE';
terminal GT : 'GT';

hidden terminal WS: /\s+/;
terminal ID: /[_a-zA-Z][\w_]*/;
terminal FLOAT returns number: /[0-9]*\.[0-9]+/;
terminal INT returns number: /[0-9]+/;
terminal STRING: /"[^"]*"|'[^']*'/;

hidden terminal ML_COMMENT: /\/\*[\s\S]*?\*\//;
hidden terminal SL_COMMENT: /\/\/[^\n\r]*/;
