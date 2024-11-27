"use strict";
/******************************************************************************
 * This file was generated by langium-cli 1.0.0.
 * DO NOT EDIT MANUALLY!
 ******************************************************************************/
Object.defineProperty(exports, "__esModule", { value: true });
exports.reflection = exports.ArduinoMlAstReflection = exports.isTransition = exports.Transition = exports.isState = exports.State = exports.isSignal = exports.Signal = exports.isSensor = exports.Sensor = exports.isOperator = exports.Operator = exports.isDigitalCondition = exports.DigitalCondition = exports.isConditionTree = exports.ConditionTree = exports.isComparator = exports.Comparator = exports.isApp = exports.App = exports.isAnalogCondition = exports.AnalogCondition = exports.isActuator = exports.Actuator = exports.isAction = exports.Action = exports.isBrick = exports.Brick = void 0;
/* eslint-disable */
const langium_1 = require("langium");
exports.Brick = 'Brick';
function isBrick(item) {
    return exports.reflection.isInstance(item, exports.Brick);
}
exports.isBrick = isBrick;
exports.Action = 'Action';
function isAction(item) {
    return exports.reflection.isInstance(item, exports.Action);
}
exports.isAction = isAction;
exports.Actuator = 'Actuator';
function isActuator(item) {
    return exports.reflection.isInstance(item, exports.Actuator);
}
exports.isActuator = isActuator;
exports.AnalogCondition = 'AnalogCondition';
function isAnalogCondition(item) {
    return exports.reflection.isInstance(item, exports.AnalogCondition);
}
exports.isAnalogCondition = isAnalogCondition;
exports.App = 'App';
function isApp(item) {
    return exports.reflection.isInstance(item, exports.App);
}
exports.isApp = isApp;
exports.Comparator = 'Comparator';
function isComparator(item) {
    return exports.reflection.isInstance(item, exports.Comparator);
}
exports.isComparator = isComparator;
exports.ConditionTree = 'ConditionTree';
function isConditionTree(item) {
    return exports.reflection.isInstance(item, exports.ConditionTree);
}
exports.isConditionTree = isConditionTree;
exports.DigitalCondition = 'DigitalCondition';
function isDigitalCondition(item) {
    return exports.reflection.isInstance(item, exports.DigitalCondition);
}
exports.isDigitalCondition = isDigitalCondition;
exports.Operator = 'Operator';
function isOperator(item) {
    return exports.reflection.isInstance(item, exports.Operator);
}
exports.isOperator = isOperator;
exports.Sensor = 'Sensor';
function isSensor(item) {
    return exports.reflection.isInstance(item, exports.Sensor);
}
exports.isSensor = isSensor;
exports.Signal = 'Signal';
function isSignal(item) {
    return exports.reflection.isInstance(item, exports.Signal);
}
exports.isSignal = isSignal;
exports.State = 'State';
function isState(item) {
    return exports.reflection.isInstance(item, exports.State);
}
exports.isState = isState;
exports.Transition = 'Transition';
function isTransition(item) {
    return exports.reflection.isInstance(item, exports.Transition);
}
exports.isTransition = isTransition;
class ArduinoMlAstReflection extends langium_1.AbstractAstReflection {
    getAllTypes() {
        return ['Action', 'Actuator', 'AnalogCondition', 'App', 'Brick', 'Comparator', 'ConditionTree', 'DigitalCondition', 'Operator', 'Sensor', 'Signal', 'State', 'Transition'];
    }
    computeIsSubtype(subtype, supertype) {
        switch (subtype) {
            case exports.Actuator:
            case exports.Sensor: {
                return this.isSubtype(exports.Brick, supertype);
            }
            default: {
                return false;
            }
        }
    }
    getReferenceType(refInfo) {
        const referenceId = `${refInfo.container.$type}:${refInfo.property}`;
        switch (referenceId) {
            case 'Action:actuator': {
                return exports.Actuator;
            }
            case 'AnalogCondition:trigger':
            case 'DigitalCondition:trigger': {
                return exports.Sensor;
            }
            case 'App:initial':
            case 'Transition:next': {
                return exports.State;
            }
            default: {
                throw new Error(`${referenceId} is not a valid reference id.`);
            }
        }
    }
    getTypeMetaData(type) {
        switch (type) {
            case 'App': {
                return {
                    name: 'App',
                    mandatory: [
                        { name: 'bricks', type: 'array' },
                        { name: 'states', type: 'array' }
                    ]
                };
            }
            case 'State': {
                return {
                    name: 'State',
                    mandatory: [
                        { name: 'actions', type: 'array' }
                    ]
                };
            }
            default: {
                return {
                    name: type,
                    mandatory: []
                };
            }
        }
    }
}
exports.ArduinoMlAstReflection = ArduinoMlAstReflection;
exports.reflection = new ArduinoMlAstReflection();
//# sourceMappingURL=ast.js.map