from pyArduinoML.model.OPERATOR import OPERATOR
from pyArduinoML.model.SIGNAL import Signal

class ToWiring:
    """
    Visitor to generate Wiring code from an ArduinoML model.
    """

    def __init__(self):
        self.result = []
        self.context = {}
        self.indent_level = 0

    def w(self, s):
        """
        Write a line of code with the current indentation level.
        """
        self.result.append("    " * self.indent_level + s)

    def increase_indent(self):
        """
        Increase the current indentation level.
        """
        self.indent_level += 1

    def decrease_indent(self):
        """
        Decrease the current indentation level.
        """
        self.indent_level = max(0, self.indent_level - 1)

    def visit_app(self, app):
        self.context['pass'] = 'ONE'
        self.w("// Wiring code generated from an ArduinoML model")
        self.w(f"// Application name: {app.name}")
        self.w("long debounce = 200;")
        self.w("enum STATE {")
        self.increase_indent()
        sep = ""
        for state in app.states:
            self.w(sep)
            state.accept(self)
            sep = ", "
        self.decrease_indent()
        self.w("};")
        if app.initial:
            self.w(f"STATE currentState = {app.initial.name};")
        for brick in app.bricks:
            brick.accept(self)

        # Second pass: setup and loop
        self.context['pass'] = 'TWO'
        self.w("void setup() {")
        self.increase_indent()
        for brick in app.bricks:
            brick.accept(self)
        self.decrease_indent()
        self.w("}")
        self.w("void loop() {")
        self.increase_indent()
        self.w("switch(currentState) {")
        self.increase_indent()
        for state in app.states:
            state.accept(self)
        self.decrease_indent()
        self.w("}")
        self.decrease_indent()
        self.w("}")

    def visit_actuator(self, actuator):
        if self.context['pass'] == 'TWO':
            self.w(f"pinMode({actuator.pin}, OUTPUT); // {actuator.name} [Actuator]")

    def visit_sensor(self, sensor):
        if self.context['pass'] == 'ONE':
            self.w(f"boolean {sensor.name}BounceGuard = false;")
            self.w(f"long {sensor.name}LastDebounceTime = 0;")
        elif self.context['pass'] == 'TWO':
            self.w(f"pinMode({sensor.pin}, INPUT); // {sensor.name} [Sensor]")

    def visit_state(self, state):
        if self.context['pass'] == 'ONE':
            self.w(state.name)
        elif self.context['pass'] == 'TWO':
            self.w(f"case {state.name}:")
            self.increase_indent()
            for action in state.actions:
                action.accept(self)
            if state.transition:
                state.transition.accept(self)
            self.w("break;")
            self.decrease_indent()

    def visit_signal_transition(self, transition):
        if self.context['pass'] == 'TWO':
            if transition.condition is None:
                raise ValueError("SignalTransition is missing a condition.")
            
            unique_sensors = list(set(transition.condition.getSensors()))
            for sensor in unique_sensors:
                self.w(f"{sensor.name}BounceGuard = millis() - {sensor.name}LastDebounceTime > debounce;")

            self.w("if (")
            self.increase_indent()
            self.w(" && ".join([f"{sensor.name}BounceGuard" for sensor in unique_sensors]) + " &&")
            transition.condition.accept(self)
            self.decrease_indent()
            self.w(") {")
            self.increase_indent()
            for sensor in unique_sensors:
                self.w(f"{sensor.name}LastDebounceTime = millis();")
            self.w(f"currentState = {transition.next.name};")
            self.decrease_indent()
            self.w("}")

    def visit_boolean_operator(self, operator):
        op_str = "&&" if operator == OPERATOR.AND else "||"
        self.w(op_str)

    def visit_digital_condition(self, condition):
        sensor_pin = condition.sensor.pin
        signal_value = "HIGH" if condition.value == Signal.HIGH else "LOW"
        self.w(f"digitalRead({sensor_pin}) == {signal_value}")

    def visit_time_transition(self, transition):
        if self.context['pass'] == 'TWO':
            self.w(f"delay({transition.delay});")
            self.w(f"currentState = {transition.next.name};")

    def visit_action(self, action):
        if self.context['pass'] == 'TWO':
            self.w(f"digitalWrite({action.actuator.pin}, {action.value});")

    def get_result(self):
        return "\n".join(self.result)
