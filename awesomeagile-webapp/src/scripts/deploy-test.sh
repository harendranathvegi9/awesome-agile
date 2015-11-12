#!/bin/sh

# set DOCKER_HUB_USERNAME, DOCKER_HUB_PASSWORD, and DOCKER_HUB_EMAIL

mvn package -DskipTests

mvn -pl org.awesomeagile:awesomeagile-webapp \
  -DskipTests \
  --settings awesomeagile-webapp/src/scripts/maven_settings.xml \
  docker:build docker:push

# set AWS_ACCESS_KEY and AWS_SECRET_ACCESS_KEY

#curl -o ecs-cli https://s3.amazonaws.com/amazon-ecs-cli/ecs-cli-darwin-amd64-latest
#chmod +x ecs-cli

#./ecs-cli configure --region us-east-1 --access-key $AWS_ACCESS_KEY --secret-key $AWS_SECRET_ACCESS_KEY --cluster awesomeagile-test

