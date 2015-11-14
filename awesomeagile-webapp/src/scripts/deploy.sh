#!/bin/sh

# set DOCKER_HUB_USERNAME, DOCKER_HUB_PASSWORD, and DOCKER_HUB_EMAIL

echo "======== Deploying awesomeagile-webapp to $1 environment"
echo "======== Installing the AWS CLI"
pip install --user awscli \
&& \
export PATH=$PATH:$HOME/.local/bin \
&& \
echo "======== Packaging"
mvn package -DskipTests \
&& \
echo "======== Building and pushing Docker Image" \
&& \
mvn -pl org.awesomeagile:awesomeagile-webapp \
  -DskipTests \
  --settings awesomeagile-webapp/src/scripts/maven_settings.xml \
  docker:build docker:push \
&& \
echo "======== Revising the AWS ECS Task Definition" \
&& \
aws ecs register-task-definition \
  --cli-input-json "`awesomeagile-webapp/src/scripts/get-task-definition.sh $1`" \
  >> /dev/null \
&& \
echo "======== Updating the AWS ECS Service to use the latest Task Definition" \
&& \
aws ecs update-service \
  --cluster $1\
  --service awesomeagile-webapp \
  --task-definition awesomeagile-webapp \
  >> /dev/null \
&& \
echo "======== Deployment complete" \
|| \
echo "======== Deployment Error"
