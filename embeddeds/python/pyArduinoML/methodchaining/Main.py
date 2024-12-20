__author__ = 'pascalpoizat'
from pyArduinoML.methodchaining.AppBuilder import AppBuilder
from pyArduinoML.generator.ToWiring import ToWiring
"""
DSL version of the demo application
uses MethodChaining, nothing Python-specific
"""


def state_based_alarm():
    """
    Direct use of the DSL.
    + : auto-completion (limited due to python typing system)
    - : verbose, Python syntax requires '\' to cut lines.

    :return:
    """
    app = (
        AppBuilder.application("RedButton")
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

def very_simple_alarm():
    """
    Demonstrates the VerySimpleAlarm scenario in Python.
    """

    # Define the app
    app = (
        AppBuilder.application("VerySimpleAlarm")
        .uses(AppBuilder.sensor("button", 9))
        .uses(AppBuilder.actuator("led", 10))
        .uses(AppBuilder.actuator("buzzer", 11))
        
        .hasForState("pressed")
            .setting("led").toHigh()
            .setting("buzzer").toHigh()
        .endState()
        
        .hasForState("unpressed").initial()
            .setting("led").toLow()
            .setting("buzzer").toLow()
        .endState()
        
        
        .beginTransitionTable()
            .from_("pressed")
                .when("button").isLow().end_when()
            
            .go_to("unpressed")
            .from_("unpressed")
                .when("button").isHigh().end_when()
            .go_to("pressed")
        .endTransitionTable()
        .build()
    )
    # Generate Wiring code using ToWiring visitor
    visitor = ToWiring()
    app.accept(visitor)
    generated_code = visitor.get_result()

    # Print the generated Wiring code
    print(generated_code)

def multi_state_alarm():
    app = (
        AppBuilder.application("MultiStateAlarm")
        .uses(AppBuilder.sensor("button", 9))
        .uses(AppBuilder.actuator("led", 11))
        .uses(AppBuilder.actuator("buzzer", 12))
        
        .hasForState("initial").initial()
            .setting("led").toLow()
            .setting("buzzer").toLow()
        .endState()
        
        .hasForState("buzzerOn")
            .setting("buzzer").toHigh()
        .endState()
        
        .hasForState("ledOn")
            .setting("led").toHigh()
            .setting("buzzer").toLow()
        .endState()
        
        .beginTransitionTable()
            .from_("initial")
                .when("button").isHigh().end_when()
            .go_to("buzzerOn")
            
            .from_("buzzerOn")
                .when("button").isHigh().end_when()
            .go_to("ledOn")
            
            .from_("ledOn")
                .when("button").isHigh().end_when()
            .go_to("initial")
        .endTransitionTable()
        .build()
    )
    
    # Generate Wiring code using ToWiring visitor
    visitor = ToWiring()
    app.accept(visitor)
    generated_code = visitor.get_result()

    # Print the generated Wiring code
    print(generated_code)
    
def dual_check_alarm():
    app = (
        AppBuilder.application("DualCheckAlarm")
            .uses(AppBuilder.sensor("button1", 9))
            .uses(AppBuilder.sensor("button2", 10))
            .uses(AppBuilder.actuator("led", 11))
            .hasForState("pressed")
                .setting("led").toHigh()
            .endState()

            .hasForState("unpressed").initial()
                .setting("led").toLow()
            .endState()

            .beginTransitionTable()
                .from_("unpressed")
                    .when("button1").isHigh().and_().add_sensor("button2").isHigh().end_when()
                .go_to("pressed") 

                .from_("pressed") 
                    .when("button1").isLow().or_().add_sensor("button2").isLow().end_when()
                .go_to("unpressed")
            .endTransitionTable()
            .build()
    )
    
    # Generate Wiring code using ToWiring visitor
    visitor = ToWiring()
    app.accept(visitor)
    generated_code = visitor.get_result()

    # Print the generated Wiring code
    print(generated_code)

def fire_detection():
    app = (
        AppBuilder.application("FireDetection")
            .uses(AppBuilder.sensor("thermometre", 1)) 
            .uses(AppBuilder.actuator("alarm", 11))
            .hasForState("idle").initial()
                .setting("alarm").toLow()
            .endState()

            .hasForState("fireDetected") 
                .setting("alarm").toHigh()
            .endState()

            .beginTransitionTable()
                .from_("idle")
                    .when("thermometre").greater_than("57") 
                    .end_when()
                .go_to("fireDetected")

                .from_("fireDetected")
                    .when("thermometre").less_or_equals("57")
                    .end_when()
                .go_to("idle")
            .endTransitionTable()
            .build()
    )

    # Generate Wiring code using ToWiring visitor
    visitor = ToWiring()
    app.accept(visitor)
    generated_code = visitor.get_result()

    # Print the generated Wiring code
    print(generated_code)

# if __name__ == '__main__':
    # red_button_application()

