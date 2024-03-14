# Use the OpenJDK 21 image as the base
FROM openjdk:21 as build

# Copy the Maven Wrapper and pom.xml to the image
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Ensure that the Maven Wrapper has execute permissions
RUN chmod +x mvnw

# Download all the dependencies in the go-offline phase
RUN ./mvnw dependency:go-offline -B

# Copy the source code to the image
COPY src src

# Package the application without running tests
RUN ./mvnw package -DskipTests

# Use the JRE 21 base image for the runtime
FROM openjdk:21

# Copy the packaged JAR file to the runtime container
COPY --from=build /target/*.jar app.jar

# Set the entry point to run the application
ENTRYPOINT ["java","-jar","/app.jar"]
