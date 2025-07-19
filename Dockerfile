FROM eclipse-temurin:21-jdk

# Set the working directory
WORKDIR /app

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run relevant required installations
RUN apt-get update && \
    apt-get install -y ffmpeg gettext curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy the JAR file
ARG JAR_FILE=target/project_video-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar

COPY entrypoint.sh /entrypoint.sh
COPY service-account.template.json /app/secrets/service-account.template.json

RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]