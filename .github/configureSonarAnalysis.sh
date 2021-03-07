#!/bin/bash

if [ "$GITHUB_ACTIONS" != "true" ]; then
  echo "warn: looks like not running inside a github actions workflow."
fi

case "$GITHUB_EVENT_NAME" in
  "push")
    export SONAR_ANALYSIS_TYPE="branch" && echo "SONAR_ANALYSIS_TYPE=$SONAR_ANALYSIS_TYPE" >> $GITHUB_ENV
    export SONAR_BRANCH_NAME=$(echo "$GITHUB_REF" | sed -e "s/^refs\/heads\///") && echo "SONAR_BRANCH_NAME=$SONAR_BRANCH_NAME" >> $GITHUB_ENV
    ;;
  "pull_request")
    export SONAR_ANALYSIS_TYPE="pull_request" && echo "SONAR_ANALYSIS_TYPE=$SONAR_ANALYSIS_TYPE" >> $GITHUB_ENV
    export SONAR_PULLREQUEST_KEY=$(echo "$GITHUB_REF" | sed -e "s/^refs\/pull\///" -e "s/\/merge$//") && echo "SONAR_PULLREQUEST_KEY=$SONAR_PULLREQUEST_KEY" >> $GITHUB_ENV
    export GITHUB_SHA=$(git rev-parse "$GITHUB_HEAD_REF") && echo "GITHUB_SHA=$GITHUB_SHA" >> $GITHUB_ENV # workaround for the throw-away merge commit
    ;;
  *)
    echo "warn: unknown GITHUB_EVENT_NAME=$GITHUB_EVENT_NAME"
    ;;
esac

if [ "$SONAR_BRANCH_NAME" != "master" ]; then # allow to compare the current branch against master
  git fetch --no-tags origin +refs/heads/master:refs/remotes/origin/master
fi
