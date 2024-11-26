import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;

import static io.github.mosser.arduinoml.embedded.java.dsl.AppBuilder.*;

public class FireDetection {
    public static void main (String[] args) {

        App myApp =
                application("FireDetection")
                        .uses(sensor("thermometre", 9))
                        .uses(actuator("alarm", 11)) // Corresponds in real life to a buzzer or a led for example
                        .hasForState("idle").initial()
                            .setting("alarm").toLow()
                        .endState()
                        .hasForState("fireDetected")
                            .setting("alarm").toHigh()
                        .endState()
                        .beginTransitionTable()
                            .from("idle")
                                .when().sensor("thermometre").greaterThan("57")
                                .endWhen()
                            .goTo("fireDetected")
                            .from("fireDetected")
                                .when().sensor("thermometre").lessOrEquals("57")
                                .endWhen()
                            .goTo("idle")
                        .endTransitionTable()
                .build();


        Visitor codeGenerator = new ToWiring();
        myApp.accept(codeGenerator);
        System.out.println(codeGenerator.getResult());
    }
}
