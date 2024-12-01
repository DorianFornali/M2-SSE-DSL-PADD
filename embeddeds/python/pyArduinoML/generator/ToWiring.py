class ToWiring:
    """
    Visitor to generate Wiring code from an ArduinoML model.
    """

    def __init__(self):
        self.result = []
        self.context = {}

    def w(self, s):
        
        self.result.append(s)

    def visit_app(self, app):
        self.context['pass'] = 'ONE'
        self.w("// Wiring code generated from an ArduinoML model")
        self.w(f"// Application name: {app.name}\n")

        self.w("long debounce = 200;\n")
        self.w("\nenum STATE {")
        sep = ""
        for state in app.states:
            self.w(sep)
            state.accept(self)
            sep = ", "
        self.w("};\n")
        if app.initial:
            self.w(f"STATE currentState = {app.initial.name};\n")

        for brick in app.bricks:
            brick.accept(self)

        # Second pass: setup and loop
        self.context['pass'] = 'TWO'
        self.w("\nvoid setup() {")
        for brick in app.bricks:
            brick.accept(self)
        self.w("}\n")

        self.w("\nvoid loop() {\n\tswitch(currentState) {")
        for state in app.states:
            state.accept(self)
        self.w("\t}\n}")

    def visit_actuator(self, actuator):
        """
        Visit an Actuator and generate Wiring code.

        :param actuator: Actuator instance.
        """
        if self.context['pass'] == 'TWO':
            self.w(f"  pinMode({actuator.pin}, OUTPUT); // {actuator.name} [Actuator]")

    def visit_sensor(self, sensor):
        """
        Visit a Sensor and generate Wiring code.

        :param sensor: Sensor instance.
        """
        if self.context['pass'] == 'ONE':
            self.w(f"\nboolean {sensor.name}BounceGuard = false;")
            self.w(f"long {sensor.name}LastDebounceTime = 0;")
        elif self.context['pass'] == 'TWO':
            self.w(f"  pinMode({sensor.pin}, INPUT);  // {sensor.name} [Sensor]")

    def visit_state(self, state):
        """
        Visit a State and generate Wiring code.

        :param state: State instance.
        """
        if self.context['pass'] == 'ONE':
            self.w(state.name)
        elif self.context['pass'] == 'TWO':
            self.w(f"\t\tcase {state.name}:")
            for action in state.actions:
                action.accept(self)
            if state.transition:
                state.transition.accept(self)
                self.w("\t\tbreak;")

    def visit_signal_transition(self, transition):
        """
        Visit a SignalTransition and generate Wiring code.

        :param transition: SignalTransition instance.
        """
        if self.context['pass'] == 'TWO':
            sensor_name = transition.sensor.name
            self.w(f"\t\t\t{sensor_name}BounceGuard = millis() - {sensor_name}LastDebounceTime > debounce;")
            self.w(f"\t\t\tif (digitalRead({transition.sensor.pin}) == {transition.value} && {sensor_name}BounceGuard) {{")
            self.w(f"\t\t\t\t{sensor_name}LastDebounceTime = millis();")
            self.w(f"\t\t\t\tcurrentState = {transition.next.name};")
            self.w("\t\t\t}")

    def visit_time_transition(self, transition):
        """
        Visit a TimeTransition and generate Wiring code.

        :param transition: TimeTransition instance.
        """
        if self.context['pass'] == 'TWO':
            self.w(f"\t\t\tdelay({transition.delay});")
            self.w(f"\t\t\tcurrentState = {transition.next.name};")

    def visit_action(self, action):
        """
        Visit an Action and generate Wiring code.

        :param action: Action instance.
        """
        if self.context['pass'] == 'TWO':
            self.w(f"\t\t\tdigitalWrite({action.actuator.pin}, {action.value});")

    def get_result(self):
        """
        Get the generated Wiring code.

        :return: str, the generated code.
        """
        return "\n".join(self.result)
