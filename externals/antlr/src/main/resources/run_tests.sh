#!/bin/sh

cd ../../..

mvn clean package

# Run the tests
mvn exec:java -Dexec.args="src/main/resources/red_button.arduinoml"
mvn exec:java -Dexec.args="src/main/resources/simple_alarm.arduinoml"
mvn exec:java -Dexec.args="src/main/resources/dual_check_alarm.arduinoml"
mvn exec:java -Dexec.args="src/main/resources/state_based_alarm.arduinoml"
mvn exec:java -Dexec.args="src/main/resources/multi_state_alarm.arduinoml"
mvn exec:java -Dexec.args="src/main/resources/fire_detection.arduinoml"
mvn exec:java -Dexec.args="src/main/resources/auto_pin_allocation.arduinoml"
