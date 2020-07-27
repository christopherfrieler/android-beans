#!/bin/bash

if [ "$GITHUB_ACTIONS" != "true" ]; then
  echo "warn: looks like not running inside a github actions workflow."
fi

case "$GITHUB_EVENT_NAME" in
  "push")
    export SONAR_ANALYSIS_TYPE="branch"
    export SONAR_BRANCH_NAME=$(echo "$GITHUB_REF" | sed -e "s/^refs\/heads\///")
    ;;
  "pull_request")
    export SONAR_ANALYSIS_TYPE="pull_request"
    export SONAR_PULLREQUEST_KEY=$(echo "$GITHUB_REF" | sed -e "s/^refs\/pull\///" -e "s/\/merge$//")
    ;;
  *)
    echo "warn: unknown GITHUB_EVENT_NAME=$GITHUB_EVENT_NAME"
    ;;
esac

if [ "$SONAR_BRANCH_NAME" != "master" ]; then # allow to compare the current branch against master
  git fetch --no-tags origin +refs/heads/master:refs/remotes/origin/master
fi
