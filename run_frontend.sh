#!/bin/bash

JAVA_OPTS="-javaagent:$pwd/dd-java-agent.jar"
./target/playBinary/bin/playBinary
