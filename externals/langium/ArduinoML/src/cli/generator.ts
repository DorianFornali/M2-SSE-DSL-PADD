import fs from 'fs';
import { CompositeGeneratorNode, NL, toString } from 'langium';
import path from 'path';
import { Action, Actuator, App, Condition, ConditionTree, Sensor, State, Transition } from '../language-server/generated/ast';
import { extractDestinationAndName } from './cli-util';

export function generateInoFile(app: App, filePath: string, destination: string | undefined): string {
    const data = extractDestinationAndName(filePath, destination);
    const generatedFilePath = `${path.join(data.destination, data.name)}.ino`;

    const fileNode = new CompositeGeneratorNode();
    compile(app,fileNode)
    
    
    if (!fs.existsSync(data.destination)) {
        fs.mkdirSync(data.destination, { recursive: true });
    }
    fs.writeFileSync(generatedFilePath, toString(fileNode));
    return generatedFilePath;
}


function compile(app:App, fileNode:CompositeGeneratorNode){
    fileNode.append(
	`
//Wiring code generated from an ArduinoML model
// Application name: `+app.name+`

long debounce = 200;
enum STATE {`+app.states.map(s => s.name).join(', ')+`};

STATE currentState = `+app.initial.ref?.name+`;`
    ,NL);
	
    for(const brick of app.bricks){
        if ("inputPin" in brick){
            fileNode.append(`
bool `+brick.name+`BounceGuard = false;
long `+brick.name+`LastDebounceTime = 0;

            `,NL);
        }
    }
    fileNode.append(`
	void setup(){`);
    for(const brick of app.bricks){
        if ("inputPin" in brick){
       		compileSensor(brick,fileNode);
		}else{
            compileActuator(brick,fileNode);
        }
	}


    fileNode.append(`
	}
	void loop() {
			switch(currentState){`,NL)
			for(const state of app.states){
				compileState(state, fileNode)
            }
	fileNode.append(`
		}
	}
	`,NL);




    }

	function compileActuator(actuator: Actuator, fileNode: CompositeGeneratorNode) {
        fileNode.append(`
		pinMode(`+actuator.outputPin+`, OUTPUT); // `+actuator.name+` [Actuator]`)
    }

	function compileSensor(sensor:Sensor, fileNode: CompositeGeneratorNode) {
    	fileNode.append(`
		pinMode(`+sensor.inputPin+`, INPUT); // `+sensor.name+` [Sensor]`)
	}

    function compileState(state: State, fileNode: CompositeGeneratorNode) {
        fileNode.append(`
				case `+state.name+`:`)
		for(const action of state.actions){
			compileAction(action, fileNode)
		}
		if (state.transition !== null){
			compileTransition(state.transition, fileNode)
		}
		fileNode.append(`
				break;`)
    }
	

	function compileAction(action: Action, fileNode:CompositeGeneratorNode) {
		fileNode.append(`
					digitalWrite(`+action.actuator.ref?.outputPin+`,`+action.value.value+`);`)
	}

    function compileTransition(transition: Transition, fileNode: CompositeGeneratorNode) {
        fileNode.append(`
            bool conditionMet = false;
            bool bounceGuard = false;
        `);
    
        // Compile the condition tree with debounce logic
        compileConditionTree(transition.conditionTree, fileNode, transition.conditionTree.root.trigger.ref?.name);
    
        fileNode.append(`
            if (conditionMet && bounceGuard) {
                currentState = ` + transition.next.ref?.name + `;
            }
        `);
    }
    
    function compileConditionTree(conditionTree: ConditionTree, fileNode: CompositeGeneratorNode, sensorName?: string) {
        if (conditionTree.right === undefined) {
            // Single condition with debounce
            compileCondition(conditionTree.root, fileNode, "conditionMet", sensorName);
        } else {
            // Two conditions with an operator and debounce
            const leftConditionVar = "leftCondition";
            const rightConditionVar = "rightCondition";
    
            // Compile both conditions into their own variables
            compileCondition(conditionTree.root, fileNode, leftConditionVar, sensorName);
            compileCondition(conditionTree.right, fileNode, rightConditionVar, sensorName);
    
            // Combine the conditions based on the operator
            const operatorCode =
                conditionTree.operator!.value === "AND"
                    ? `${leftConditionVar} && ${rightConditionVar}`
                    : `${leftConditionVar} || ${rightConditionVar}`;
            fileNode.append(`
                conditionMet = ${operatorCode};
            `);
        }
    }
    
    function compileCondition(condition: Condition, fileNode: CompositeGeneratorNode, resultVar: string, sensorName?: string) {
        const sensorPin = condition.trigger.ref?.inputPin;
        const signalValue = condition.value.value;
    
        fileNode.append(`
            bool ${resultVar} = (digitalRead(${sensorPin}) == ${signalValue});
    
            bool ${sensorName}BounceGuard = millis() - ${sensorName}LastDebounceTime > debounce;
            if (${resultVar} && ${sensorName}BounceGuard) {
                ${sensorName}LastDebounceTime = millis();
                bounceGuard = true;
            }
        `);
    }
    

