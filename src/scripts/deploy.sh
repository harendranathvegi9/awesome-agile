#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

# set DOCKER_HUB_USERNAME, DOCKER_HUB_PASSWORD, and DOCKER_HUB_EMAIL

error() {
  echo "======== Deployment Error"
}
trap error ERR

if [ $# -ne 2 ]; then
  echo "usage: deploy.sh <environment> <tag>"
  exit 1
fi

echo "======== Deploying awesomeagile to $1 environment"
echo "======== Installing the AWS CLI"
pip install --user awscli
export PATH=$PATH:$HOME/.local/bin

echo "======== Packaging"
mvn package -DskipTests

echo "======== Building and pushing Docker Image"
mvn \
  -DskipTests \
  --settings src/scripts/maven_settings.xml \
  -Ddocker.tag="$2" \
  docker:build docker:push

echo "======== Revising the AWS ECS Task Definition"
aws ecs register-task-definition \
  --cli-input-json "`src/scripts/get-task-definition.sh $1 $2`" \
  >> /dev/null

echo "======== Updating the AWS ECS Service to use the latest Task Definition"
aws ecs update-service \
  --cluster "$1" \
  --service "awesomeagile-$1" \
  --task-definition "awesomeagile-$1" \
  >> /dev/null

echo "======== Deployment complete"
