#!/bin/bash

curl --insecure --data-binary @$1 localhost:8080/spacegeek

#curl --insecure --data-binary @$1 https://goodbye-dot-alexa-skills-kit.appspot.com/goodbye
