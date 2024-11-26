import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;

import static io.github.mosser.arduinoml.embedded.java.dsl.AppBuilder.*;

public class MultiStateAlarm {
    public static void main (String[] args) {

        App myApp =
                application("MultiStateAlarm")
                        .uses(sensor("button", 9))
                        .uses(actuator("led", 11))
                        .uses(actuator("buzzer", 12))
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
                            .from("initial")
                                .when().sensor("button").equals("HIGH")
                                .endWhen()
                            .goTo("buzzerOn")
                            .from("buzzerOn")
                                .when().sensor("button").equals("HIGH")
                                .endWhen()
                            .goTo("ledOn")
                            .from("ledOn")
                                .when().sensor("button").equals("HIGH")
                                .endWhen()
                            .goTo("initial")
                        .endTransitionTable()
                .build();


        Visitor codeGenerator = new ToWiring();
        myApp.accept(codeGenerator);
        System.out.println(codeGenerator.getResult());
    }
}
