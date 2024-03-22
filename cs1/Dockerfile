FROM maven:3.9.6 AS build

COPY pom.xml /build/pom.xml
COPY cs1/pom.xml /build/cs1/pom.xml

COPY cs1/src /build/cs1/src
COPY postgres-init/wait-for-postgres.sh /usr/local/bin/

WORKDIR /build/cs1

RUN mvn clean package -DskipTests

FROM openjdk:21

COPY --from=build /build/cs1/target/cs1-*.jar /app/cs1.jar

ENTRYPOINT ["java","-jar","/app/cs1.jar"]
