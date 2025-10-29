FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

ARG APP_JAR=expense-manager-0.0.1-SNAPSHOT.jar
COPY --from=build /workspace/target/${APP_JAR} app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
