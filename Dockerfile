FROM openjdk:21 AS build

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw package -DskipTests

FROM openjdk:21

COPY --from=build /target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
