#!/bin/sh

# Get the file to generate
if [ -z "$1" ]; then
  echo "Error: No file name provided."
  echo "Usage: $0 <FILE_NAME>"
  exit 1
fi

FILE_NAME=$1

# Function to replace back package and import statements
restore_statements() {
  find src/main/groovy/groovuinoml/dsl -type f -exec sed -i '' 's/package main.groovy.groovuinoml.dsl/package groovuinoml.dsl/g' {} +
  find src/main/groovy/groovuinoml/main -type f -exec sed -i '' 's/package main.groovy.groovuinoml.main/package groovuinoml.main/g' {} +
  find src/main/groovy/groovuinoml/main -type f -exec sed -i '' 's/import main.groovy.groovuinoml.dsl.GroovuinoMLDSL;/import groovuinoml.dsl.GroovuinoMLDSL;/g' {} +
}

# Replace package and import statements
find src/main/groovy/groovuinoml/dsl -type f -exec sed -i '' 's/package groovuinoml.dsl/package main.groovy.groovuinoml.dsl/g' {} +
find src/main/groovy/groovuinoml/main -type f -exec sed -i '' 's/package groovuinoml.main/package main.groovy.groovuinoml.main/g' {} +
find src/main/groovy/groovuinoml/main -type f -exec sed -i '' 's/import groovuinoml.dsl.GroovuinoMLDSL;/import main.groovy.groovuinoml.dsl.GroovuinoMLDSL;/g' {} +

# Compile code
mvn clean compile assembly:single
if [ $? -ne 0 ]; then
  restore_statements
  exit 1
fi

# Execute with the provided file
mkdir -p generated
touch generated/$FILE_NAME.ino
java -jar target/dsl-groovy-1.0-jar-with-dependencies.jar scripts/$FILE_NAME.groovy > generated/$FILE_NAME.ino
if [ $? -ne 0 ]; then
  restore_statements
  exit 1
fi

# Replace back package and import statements
restore_statements