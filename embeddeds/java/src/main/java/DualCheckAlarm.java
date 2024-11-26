import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;

import static io.github.mosser.arduinoml.embedded.java.dsl.AppBuilder.*;
import static io.github.mosser.arduinoml.embedded.java.dsl.AppBuilder.actuator;

public class DualCheckAlarm {
    public static void main (String[] args) {

        App myApp =
                application("VerySimpleAlarm")
                        .uses(sensor("button1", 9))
                        .uses(sensor("button2", 10))
                        .uses(actuator("led", 11))
                        .hasForState("pressed")
                            .setting("led").toHigh()
                        .endState()
                        .hasForState("unpressed").initial()
                            .setting("led").toLow()
                        .endState()
                        .beginTransitionTable()
                            .from("unpressed")
                                .when().sensor("button1").equals("HIGH")
                                .and().sensor("button2").equals("HIGH")
                                .endWhen()
                            .goTo("pressed")
                            .from("pressed")
                                .when().sensor("button1").equals("LOW")
                                .or().sensor("button2").equals("LOW")
                                .endWhen()
                            .goTo("unpressed")
                        .endTransitionTable()
                .build();


        Visitor codeGenerator = new ToWiring();
        myApp.accept(codeGenerator);
        System.out.println(codeGenerator.getResult());
    }
}
