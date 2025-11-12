#!/bin/bash
cd /home/kavia/workspace/code-generation/ludo-online-223670-223680/backend
./gradlew checkstyleMain
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

