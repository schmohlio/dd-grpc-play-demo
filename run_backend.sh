#!/bin/bash

export JAVA_OPTS="-javaagent:/Users/matthewschmohl/dd-grpc-play-demo/dd-java-agent.jar -Ddd.integrations.enabled=true -Ddd.service.name=refbackend -Ddd.logs.injection=true"
./target/backend-1.0/bin/backend
