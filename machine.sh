#!/bin/bash
MACHINE_NAME=awesome-agile
docker-machine status ${MACHINE_NAME}
if [ $? -ne 0 ]; then
  docker-machine create --driver virtualbox ${MACHINE_NAME}
fi
eval $(docker-machine env ${MACHINE_NAME})
