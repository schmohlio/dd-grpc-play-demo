#!/bin/bash

export JAVA_OPTS="-javaagent:$(pwd)/dd-java-agent.jar -Ddd.integration.grpc.enabled=false -Ddd.service.name=refbackend -Ddd.logs.injection=true -Ddatadog.slf4j.simpleLogger.defaultLogLevel=debug"
./target/backend-1.0/bin/backend 2>&1 | tee $(pwd)/logs/backend.txt
