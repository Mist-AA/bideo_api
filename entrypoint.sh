#!/bin/sh

echo "Generating service-account.json from template..."

envsubst < /app/secrets/service-account.template.json > /app/secrets/service-account.json

echo "Starting Spring Boot application..."
exec java -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar