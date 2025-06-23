#!/bin/bash

# Check if .env file exists
if [ -f "./.env" ]; then
    # Load environment variables from .env
    export $(grep -v '^#' ./.env | xargs)
fi

# Run the application
./gradlew bootRun
