#!/bin/bash

./gradlew clean build dist 
mkdir ./target
tar -xf frontend/build/distributions/playBinary.tar -C ./target
tar -xf backend/build/distributions/backend-1.0.tar -C ./target
