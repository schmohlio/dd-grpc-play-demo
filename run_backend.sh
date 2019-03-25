#!/bin/bash

JAVA_OPTS="-javaagent:$pwd/dd-java-agent.jar"
./target/backend-1.0/bin/backend
