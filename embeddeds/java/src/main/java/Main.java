import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.OPERATOR;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;

import static io.github.mosser.arduinoml.embedded.java.dsl.AppBuilder.*;

public class Main {


    public static void main (String[] args) {

        App myApp =
                application("red_button")
                        .uses(sensor("button", 9))
                        .uses(actuator("led", 12))
                        .hasForState("on")
                            .setting("led").toHigh()
                        .endState()
                        .hasForState("off").initial()
                            .setting("led").toLow()
                        .endState()
                        .beginTransitionTable()
                            .from("on").when().sensor("button").equals()
                        .value(SIGNAL.HIGH)
                        .and()
                        .sensor("button")
                        .equals()
                        .value(SIGNAL.LOW)
                        .endConditionTree()
                        .closeWhen()
                        .goTo("off")
                        .endTransitionTable()
                .build();


        Visitor codeGenerator = new ToWiring();
        myApp.accept(codeGenerator);
        System.out.println(codeGenerator.getResult());
    }

}
