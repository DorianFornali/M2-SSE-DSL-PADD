"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.generateInoFile = void 0;
const fs_1 = __importDefault(require("fs"));
const langium_1 = require("langium");
const path_1 = __importDefault(require("path"));
const cli_util_1 = require("./cli-util");
function generateInoFile(app, filePath, destination) {
    const data = (0, cli_util_1.extractDestinationAndName)(filePath, destination);
    const generatedFilePath = `${path_1.default.join(data.destination, data.name)}.ino`;
    const fileNode = new langium_1.CompositeGeneratorNode();
    compile(app, fileNode);
    if (!fs_1.default.existsSync(data.destination)) {
        fs_1.default.mkdirSync(data.destination, { recursive: true });
    }
    fs_1.default.writeFileSync(generatedFilePath, (0, langium_1.toString)(fileNode));
    return generatedFilePath;
}
exports.generateInoFile = generateInoFile;
function compile(app, fileNode) {
    var _a;
    fileNode.append(`
//Wiring code generated from an ArduinoML model
// Application name: ` + app.name + `

long debounce = 200;
enum STATE {` + app.states.map(s => s.name).join(', ') + `};

STATE currentState = ` + ((_a = app.initial.ref) === null || _a === void 0 ? void 0 : _a.name) + `;`, langium_1.NL);
    for (const brick of app.bricks) {
        if ("inputPin" in brick) {
            fileNode.append(`
bool ` + brick.name + `BounceGuard = false;
long ` + brick.name + `LastDebounceTime = 0;

            `, langium_1.NL);
        }
    }
    fileNode.append(`
	void setup(){`);
    for (const brick of app.bricks) {
        if ("inputPin" in brick) {
            compileSensor(brick, fileNode);
        }
        else {
            compileActuator(brick, fileNode);
        }
    }
    fileNode.append(`
	}
	void loop() {
			switch(currentState){`, langium_1.NL);
    for (const state of app.states) {
        compileState(state, fileNode);
    }
    fileNode.append(`
		}
	}
	`, langium_1.NL);
}
function compileActuator(actuator, fileNode) {
    fileNode.append(`
		pinMode(` + actuator.outputPin + `, OUTPUT); // ` + actuator.name + ` [Actuator]`);
}
function compileSensor(sensor, fileNode) {
    fileNode.append(`
		pinMode(` + sensor.inputPin + `, INPUT); // ` + sensor.name + ` [Sensor]`);
}
function compileState(state, fileNode) {
    fileNode.append(`
				case ` + state.name + `:`);
    for (const action of state.actions) {
        compileAction(action, fileNode);
    }
    if (state.transition !== null) {
        compileTransition(state.transition, fileNode);
    }
    fileNode.append(`
				break;`);
}
function compileAction(action, fileNode) {
    var _a;
    fileNode.append(`
					digitalWrite(` + ((_a = action.actuator.ref) === null || _a === void 0 ? void 0 : _a.outputPin) + `,` + action.value.value + `);`);
}
function compileTransition(transition, fileNode) {
    var _a, _b;
    fileNode.append(`
            bool conditionMet = false;
            bool bounceGuard = false;
        `);
    // Compile the condition tree with debounce logic
    compileConditionTree(transition.conditionTree, fileNode, (_a = transition.conditionTree.root.trigger.ref) === null || _a === void 0 ? void 0 : _a.name);
    fileNode.append(`
            if (conditionMet && bounceGuard) {
                currentState = ` + ((_b = transition.next.ref) === null || _b === void 0 ? void 0 : _b.name) + `;
            }
        `);
}
function compileConditionTree(conditionTree, fileNode, sensorName) {
    if (conditionTree.right === undefined) {
        if ('operator' in conditionTree.root) {
            compileDigitalCondition(conditionTree.root, fileNode, "conditionMet", sensorName);
        }
        else {
            compileAnalogCondition(conditionTree.root, fileNode, "conditionMet", sensorName);
        }
    }
    else {
        const leftConditionVar = "leftCondition";
        const rightConditionVar = "rightCondition";
        if ('operator' in conditionTree.root) {
            compileDigitalCondition(conditionTree.root, fileNode, leftConditionVar, sensorName);
        }
        else {
            compileAnalogCondition(conditionTree.root, fileNode, leftConditionVar, sensorName);
        }
        if ('operator' in conditionTree.right) {
            compileDigitalCondition(conditionTree.right, fileNode, rightConditionVar, sensorName);
        }
        else {
            compileAnalogCondition(conditionTree.right, fileNode, rightConditionVar, sensorName);
        }
        const operatorCode = conditionTree.operator.value === "AND"
            ? `${leftConditionVar} && ${rightConditionVar}`
            : `${leftConditionVar} || ${rightConditionVar}`;
        fileNode.append(`
                conditionMet = ${operatorCode};
            `);
    }
}
function compileDigitalCondition(condition, fileNode, resultVar, sensorName) {
    var _a;
    const sensorPin = (_a = condition.trigger.ref) === null || _a === void 0 ? void 0 : _a.inputPin;
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
function compileAnalogCondition(condition, fileNode, resultVar, sensorName) {
    var _a;
    const sensorPin = (_a = condition.trigger.ref) === null || _a === void 0 ? void 0 : _a.inputPin;
    const signalValue = condition.value;
    const comparator = resolveComparator(condition.comparator.value);
    fileNode.append(`
            bool ${resultVar} = (analogRead(${sensorPin}) ${comparator} ${signalValue});
    
            bool ${sensorName}BounceGuard = millis() - ${sensorName}LastDebounceTime > debounce;
            if (${resultVar} && ${sensorName}BounceGuard) {
                ${sensorName}LastDebounceTime = millis();
                bounceGuard = true;
            }
        `);
}
function resolveComparator(comparator) {
    switch (comparator) {
        case 'LT':
            return '<';
        case 'LTE':
            return '<=';
        case 'GT':
            return '>';
        case 'GTE':
            return '>=';
        case 'EQ':
            return '==';
        case 'NEQ':
            return '!=';
        default:
            throw new Error('Unknown comparator: ' + comparator);
    }
}
//# sourceMappingURL=generator.js.map