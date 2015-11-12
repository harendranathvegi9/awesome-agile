#!/bin/sh

# set DOCKERHUB_USERNAME, DOCKERHUB_PASSWORD, and DOCKERHUB_EMAIL

mvn -pl org.awesomeagile:awesomeagile-webapp \
  -DskipTests \
  -Dsettings.security=awesomeagile-webapp/src/scripts/maven_security_settings.xml \
  --settings awesomeagile-webapp/src/scripts/maven_settings.xml \
  package docker:build docker:push

# set AWS_ACCESS_KEY and AWS_SECRET_ACCESS_KEY

#curl -o ecs-cli https://s3.amazonaws.com/amazon-ecs-cli/ecs-cli-darwin-amd64-latest
#chmod +x ecs-cli

#./ecs-cli configure --region us-east-1 --access-key $AWS_ACCESS_KEY --secret-key $AWS_SECRET_ACCESS_KEY --cluster awesomeagile-test

