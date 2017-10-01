#!/bin/bash

echo "Deploying for version ${TRAVIS_TAG}"
gradle --no-daemon bintrayUpload
