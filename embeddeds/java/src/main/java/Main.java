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
                        .addConstant("var1", 40)
                        .uses(sensor("button", 9))
                        .uses(actuator("led", 12))
                        .uses(actuator("chauffage", 8))
                        .uses(sensor("thermometre", 7))
                        .hasForState("on")
                            .setting("chauffage").toValue(30)
                        .endState()
                        .hasForState("off").initial()
                            .setting("led").toLow()
                        .endState()
                        .beginTransitionTable()
                            .from("on").when()
                        .openParenthesis()
                        .sensor("button")
                        .lessOrEquals("77")
                        .and()
                        .sensor("button")
                        .equals("LOW")
                        .closeParenthesis()
                        .or()
                        .openParenthesis()
                        .sensor("thermometre").greaterOrEquals("98")
                        .and().sensor("button").equals("HIGH")
                        .closeParenthesis()
                        .endWhen()
                        .goTo("off")
                        .endTransitionTable()
                .build();


        Visitor codeGenerator = new ToWiring();
        myApp.accept(codeGenerator);
        System.out.println(codeGenerator.getResult());
    }

}
