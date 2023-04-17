#!/bin/bash
CURRENT_DIR=$(pwd)
git pull
cd ../core
mvn clean install -Dmaven.test.skip=true
cd $CURRENT_DIR
mvn clean pre-integration-test -Dmaven.test.skip=true