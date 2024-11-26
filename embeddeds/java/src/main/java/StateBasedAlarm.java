import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;

import static io.github.mosser.arduinoml.embedded.java.dsl.AppBuilder.*;

public class StateBasedAlarm {
    public static void main (String[] args) {

        App myApp =
                application("StateBasedAlarm")
                        .uses(sensor("button", 9))
                        .uses(actuator("led", 11))
                        .hasForState("on")
                            .setting("led").toHigh()
                        .endState()
                        .hasForState("off").initial()
                            .setting("led").toLow()
                        .endState()
                        .beginTransitionTable()
                            .from("off")
                                .when().sensor("button").equals("HIGH")
                                .endWhen()
                            .goTo("on")
                            .from("on")
                                .when().sensor("button").equals("HIGH")
                                .endWhen()
                            .goTo("off")
                        .endTransitionTable()
                .build();


        Visitor codeGenerator = new ToWiring();
        myApp.accept(codeGenerator);
        System.out.println(codeGenerator.getResult());
    }
}
