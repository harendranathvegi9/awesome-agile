#!/bin/bash
DIRNAME="$(cd "$(dirname "$0")" && pwd -P)"
cd $DIRNAME
mvn clean install -DskipTests
mvn spring-boot:run
