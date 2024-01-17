# Use the official Gradle image to create a build artifact.
FROM gradle:7.2.0-jdk8 AS build

# Set the working directory in docker
WORKDIR /app

# Copy the Gradle properties and settings
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Download the dependencies
RUN ./gradlew dependencies

# Copy the rest of the application
COPY ./src ./src

# Build the application
RUN ./gradlew clean bootJar

# Use OpenJDK JRE for runtime
# TODO - openjdk:8-jdk-alpine으로 바꾸면, 성능이 더 괜찮아지려나?
FROM openjdk:8-jre-slim

# Set the working directory in docker
WORKDIR /app

# Copy the jar file built in the first stage
COPY --from=build /app/build/libs/*.jar app.jar

# Set the Spring profile to 'docker' to launch application-docker.yml
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE

# Command to run the application
# CMD ["java", "-jar", "app.jar" ]
CMD ["java", "-jar", "app.jar", "-Dspring.profiles.active=docker"]

