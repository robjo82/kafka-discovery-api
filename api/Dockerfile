FROM maven:3.9.6 AS build

COPY pom.xml /build/pom.xml
COPY api/pom.xml /build/api/pom.xml

COPY api/src /build/api/src

WORKDIR /build/api

RUN mvn clean package -DskipTests

FROM openjdk:21

COPY --from=build /build/api/target/api-*.jar /app/api.jar

ENTRYPOINT ["java","-jar","/app/api.jar"]
