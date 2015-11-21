#!/bin/sh

#if [ "$#" -eq 1 ]; then
#eval "cat <<EOF
#$(cat taskdef.json | sed s/%%ENV%%/$1/)
#EOF
#" 2> /dev/null
#else
#  echo "usage: get-task-definition.sh <deployment environment>"
#fi

if [ "$#" -eq 1 ]; then
eval "cat <<EOF
$(cat << EOF | sed s/%%ENV%%/$1/
{
    "family": "awesomeagile-webapp", 
    "containerDefinitions": [
        {
            "name": "awesomeagile",
            "image": "awesomeagile/awesomeagile-webapp:latest", 
            "cpu": 0,
            "memory": 512,
            "portMappings": [
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
else
  echo "usage: get-task-definition.sh <deployment environment>"
fi
