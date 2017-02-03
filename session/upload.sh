#!/bin/bash

curl --insecure --data-binary @$1 localhost:8080

#curl --insecure --data-binary @$1 https://session-dot-alexa-skills-kit.appspot.com
