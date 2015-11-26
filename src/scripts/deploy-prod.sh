#!/bin/sh

# Get tag from HEAD

TAG=`git describe --exact-match`
if [ $? -eq 0 ]; then
  src/scripts/deploy.sh PROD "$TAG"
else
  echo "Error, no tag on HEAD!"
fi
