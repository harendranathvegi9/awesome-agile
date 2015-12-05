#!/bin/sh

if [ $# -ne 2 ]; then
  echo "usage: get-task-definition.sh <deployment environment> <image tag>"
  exit 1
fi

eval "cat <<EOF
$(cat << EOF | sed s/%%ENV%%/$1/g
{
    "family": "awesomeagile-%%ENV%%",
    "containerDefinitions": [
        {
            "name": "awesomeagile",
            "image": "awesomeagile/awesomeagile:$2",
            "cpu": 0,
            "memory": 512,
            "portMappings": [
                {
                    "containerPort": 8887,
                    "hostPort": 8887,
                    "protocol": "tcp"
                },
                {
                    "containerPort": 8888,
                    "hostPort": 8888,
                    "protocol": "tcp"
                }
            ],
            "essential": true, 
            "environment": [
                {
                    "name": "SPRING_DATASOURCE_URL",
                    "value": "\${AWS_%%ENV%%_DB_URL}"
                },
                {
                    "name": "SPRING_DATASOURCE_USERNAME",
                    "value": "\${AWS_%%ENV%%_DB_USERNAME}"
                },
                {
                    "name": "SPRING_DATASOURCE_PASSWORD",
                    "value": "\${AWS_%%ENV%%_DB_PASSWORD}"
                },
                {
                    "name": "SPRING_SOCIAL_GOOGLE_CLIENTID",
                    "value": "\${AWS_%%ENV%%_GOOGLE_CLIENTID}"
                },
                {
                    "name": "SPRING_SOCIAL_GOOGLE_SECRET",
                    "value": "\${AWS_%%ENV%%_GOOGLE_SECRET}"
                },
                {
                    "name": "HACKPAD_URL",
                    "value": "\${AWS_%%ENV%%_HACKPAD_URL}"
                },
                {
                    "name": "HACKPAD_TEMPLATES_DEFNREADY",
                    "value": "\${AWS_%%ENV%%_HACKPAD_TEMPLATES_DEFNREADY}"
                },
                {
                    "name": "HACKPAD_CLIENT_ID",
                    "value": "\${AWS_%%ENV%%_HACKPAD_CLIENT_ID}"
                },
                {
                    "name": "HACKPAD_CLIENT_SECRET",
                    "value": "\${AWS_%%ENV%%_HACKPAD_CLIENT_SECRET}"
                }
            ]
        }
    ],
    "volumes": []
}
EOF
)
EOF
" 2> /dev/null