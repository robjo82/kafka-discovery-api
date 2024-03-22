FROM maven:3.9.6 AS build

COPY pom.xml /build/pom.xml
COPY cs2/pom.xml /build/cs2/pom.xml

COPY cs2/src /build/cs2/src
COPY postgres-init/wait-for-postgres.sh /usr/local/bin/

WORKDIR /build/cs2

RUN mvn clean package -DskipTests

FROM openjdk:21

COPY --from=build /build/cs2/target/cs2-*.jar /app/cs2.jar

ENTRYPOINT ["java","-jar","/app/cs2.jar"]
