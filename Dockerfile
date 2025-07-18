FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Copy the JAR file
ARG JAR_FILE=target/project_video-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar

RUN apk update && apk add --no-cache ffmpeg gettext

COPY entrypoint.sh /entrypoint.sh
COPY service-account.template.json /app/secrets/service-account.template.json

RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]