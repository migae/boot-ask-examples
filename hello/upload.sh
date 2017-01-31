#!/bin/bash

#curl --insecure --data-binary @$1 localhost:8080/hello

curl --insecure --data-binary @$1 https://hello-dot-alexa-skills-kit.appspot.com/hello
