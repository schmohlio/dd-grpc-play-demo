#!/bin/bash

export JAVA_OPTS="-javaagent:$(pwd)/dd-java-agent.jar -Ddd.integrations.enabled=true -Ddd.service.name=reffrontend -Ddd.logs.injection=true"
./target/playBinary/bin/playBinary
