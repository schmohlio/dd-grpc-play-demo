#!/bin/bash

export JAVA_OPTS="-javaagent:$(pwd)/dd-java-agent.jar -Ddd.integrations.enabled=true -Ddd.service.name=reffrontend -Ddd.logs.injection=true -Ddatadog.slf4j.simpleLogger.defaultLogLevel=debug"
./target/playBinary/bin/playBinary | tee $(pwd)/logs/frontend.txt
