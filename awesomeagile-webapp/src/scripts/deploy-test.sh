#!/bin/sh

mvn docker:build

# set DOCKERHUB_USERNAME, DOCKERHUB_PASSWORD, and DOCKERHUB_EMAIL

mvn docker:push --settings src/scripts/maven_settings.xml

# set AWS_ACCESS_KEY and AWS_SECRET_ACCESS_KEY

#curl -o ecs-cli https://s3.amazonaws.com/amazon-ecs-cli/ecs-cli-darwin-amd64-latest
#chmod +x ecs-cli

#./ecs-cli configure --region us-east-1 --access-key $AWS_ACCESS_KEY --secret-key $AWS_SECRET_ACCESS_KEY --cluster awesomeagile-test

