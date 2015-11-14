#!/bin/bash
DIRNAME="$(cd "$(dirname "$0")" && pwd -P)"
cd $DIRNAME
mvn clean install -DskipTests
cd awesomeagile-webapp; mvn spring-boot:run
