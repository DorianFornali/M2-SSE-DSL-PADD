"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.createArduinoMlServices = exports.ArduinoMlModule = void 0;
const langium_1 = require("langium");
const module_1 = require("./generated/module");
const arduino_ml_validator_1 = require("./arduino-ml-validator");
/**
 * Dependency injection module that overrides Langium default services and contributes the
 * declared custom services. The Langium defaults can be partially specified to override only
 * selected services, while the custom services must be fully specified.
 */
exports.ArduinoMlModule = {
    validation: {
        ArduinoMlValidator: () => new arduino_ml_validator_1.ArduinoMlValidator()
    }
};
/**
 * Create the full set of services required by Langium.
 *
 * First inject the shared services by merging two modules:
 *  - Langium default shared services
 *  - Services generated by langium-cli
 *
 * Then inject the language-specific services by merging three modules:
 *  - Langium default language-specific services
 *  - Services generated by langium-cli
 *  - Services specified in this file
 *
 * @param context Optional module context with the LSP connection
 * @returns An object wrapping the shared services and the language-specific services
 */
function createArduinoMlServices(context) {
    const shared = (0, langium_1.inject)((0, langium_1.createDefaultSharedModule)(context), module_1.ArduinoMlGeneratedSharedModule);
    const ArduinoMl = (0, langium_1.inject)((0, langium_1.createDefaultModule)({ shared }), module_1.ArduinoMlGeneratedModule, exports.ArduinoMlModule);
    shared.ServiceRegistry.register(ArduinoMl);
    (0, arduino_ml_validator_1.registerValidationChecks)(ArduinoMl);
    return { shared, ArduinoMl };
}
exports.createArduinoMlServices = createArduinoMlServices;
//# sourceMappingURL=arduino-ml-module.js.map