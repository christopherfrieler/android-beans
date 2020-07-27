#!/bin/bash

if [ "$TRAVIS" != "true" ]; then
  echo "warn: looks like not running inside a travis-ci build."
fi

case "$TRAVIS_EVENT_TYPE" in
  "push")
    export SONAR_ANALYSIS_TYPE="branch"
    export SONAR_BRANCH_NAME="$TRAVIS_BRANCH"
    if [ "$TRAVIS_BRANCH" != "master" ]; then # allow to compare the current branch against master
      git fetch --no-tags origin +refs/heads/master:refs/remotes/origin/master
    fi
    ;;
  "pull_request")
    exit 1
    export SONAR_ANALYSIS_TYPE="pull_request"
    export SONAR_PULLREQUEST_KEY="$TRAVIS_PULL_REQUEST"
    ;;
  *)
    echo "warn: unknown TRAVIS_EVENT_TYPE=$TRAVIS_EVENT_TYPE"
    ;;
esac
