import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;

import static io.github.mosser.arduinoml.embedded.java.dsl.AppBuilder.*;

public class VerySimpleAlarm {


    public static void main (String[] args) {

        App myApp =
                application("VerySimpleAlarm")
                        .uses(sensor("button", 9))
                        .uses(actuator("led", 10))
                        .uses(actuator("buzzer", 11))
                        .hasForState("pressed")
                            .setting("led").toHigh()
                            .setting("buzzer").toHigh()
                        .endState()
                        .hasForState("unpressed").initial()
                            .setting("led").toLow()
                            .setting("buzzer").toLow()
                        .endState()
                        .beginTransitionTable()
                            .from("pressed")
                                .when().sensor("button").equals("LOW")
                                .endWhen()
                            .goTo("unpressed")
                            .from("unpressed")
                                .when().sensor("button").equals("HIGH")
                                .endWhen()
                            .goTo("pressed")
                        .endTransitionTable()
                .build();


        Visitor codeGenerator = new ToWiring();
        myApp.accept(codeGenerator);
        System.out.println(codeGenerator.getResult());
    }

    /**
     * App myApp =
     *     application("red_button")
     *         .uses(sensor("button", 9))
     *         .uses(actuator("led", 12))
     *         .hasForState("on")
     *             .setting("led").toHigh()
     *         .endState()
     *         .hasForState("off").initial()
     *             .setting("led").toLow()
     *         .endState()
     *         .beginTransitionTable()
     *             .from("on").when("button").isHigh().goTo("off")
     *             .from("off").when("button").isHigh().goTo("on")
     *         .endTransitionTable()
     *     .build();
     */

}
