__author__ = 'pascalpoizat'

"""
DSL version of the demo application
uses MethodChaining, nothing Python-specific
"""


def red_button_application():
    """
    Direct use of the DSL.
    + : auto-completion (limited due to python typing system)
    - : verbose, Python syntax requires '\' to cut lines.

    :return:
    """
    from pyArduinoML.methodchaining.AppBuilder import AppBuilder
    from pyArduinoML.generator.ToWiring import ToWiring

    app = (
        AppBuilder.application("red_button")
        .uses(AppBuilder.sensor("button", 9))
        .uses(AppBuilder.actuator("led", 12))
        .hasForState("on")
            .setting("led").toHigh().endState()
            .hasForState("off").initial().setting("led").toLow().endState()
            .beginTransitionTable()
                .from_("on").when("button").isHigh().goTo("off")
                .from_("off").when("button").isHigh().goTo("on") 
            .endTransitionTable()
        .build() 
    )

    visitor = ToWiring()
    app.accept(visitor)
    generated_code = visitor.get_result() 

    print(generated_code)








if __name__ == '__main__':
    red_button_application()

