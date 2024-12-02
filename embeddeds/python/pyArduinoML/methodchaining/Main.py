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
            .setting("led").toHigh().endState() # insert the state in the app 
        .hasForState("off").initial().
            setting("led").toLow().endState() # insert the state in the app
        
        .beginTransitionTable()
            .from_("on").when("button").isHigh().end_when().go_to("off")
            .from_("off").when("button").isHigh().end_when().go_to("on") 
        .endTransitionTable()
        .build() 
    )

    visitor = ToWiring()
    app.accept(visitor)
    generated_code = visitor.get_result() 

    print(generated_code)


# def very_simple_alarm():
#     """
#     Demonstrates the VerySimpleAlarm scenario in Python.
#     """

#     from pyArduinoML.methodchaining.AppBuilder import AppBuilder
#     from pyArduinoML.generator.ToWiring import ToWiring

#     # Define the app
#     app = (
#         AppBuilder.application("VerySimpleAlarm")
#         .uses(AppBuilder.sensor("button1", 9))
#         .uses(AppBuilder.sensor("button2", 10))
#         .uses(AppBuilder.actuator("led", 11))
#         .hasForState("pressed")
#             .setting("led").toHigh()
#         .endState()
#         .hasForState("unpressed").initial()
#             .setting("led").toLow()
#         .endState()
#         .beginTransitionTable()
#             .from_("unpressed")
#                 .when("button1").isHigh()
#                 .and_().sensor("button2").isHigh()
#                 .end_when()
#             .go_to("pressed")
#             .from_("pressed")
#                 .when("button1").isLow()
#                 .or_().sensor("button2").isLow()
#                 .end_when()
#             .go_to("unpressed")
#         .endTransitionTable()
#         .build()
#     )



    # # Generate Wiring code using ToWiring visitor
    # visitor = ToWiring()
    # app.accept(visitor)
    # generated_code = visitor.get_result()

    # # Print the generated Wiring code
    # print(generated_code)







if __name__ == '__main__':
    red_button_application()

