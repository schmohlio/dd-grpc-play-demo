#!/bin/bash

export JAVA_OPTS="-javaagent:$(pwd)/dd-java-agent.jar -Ddd.integration.play.enabled=false -Ddd.integration.grpc.enabled=false -Ddd.service.name=reffrontend -Ddd.logs.injection=true -Ddatadog.slf4j.simpleLogger.defaultLogLevel=debug"
./target/playBinary/bin/playBinary | tee $(pwd)/logs/frontend.txt
