FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY user-service/pom.xml user-service/pom.xml
COPY animal-registry/pom.xml animal-registry/pom.xml
COPY activity-tracking/pom.xml activity-tracking/pom.xml
COPY analytics-service/pom.xml analytics-service/pom.xml

RUN mvn -pl animal-registry -am dependency:go-offline -B -q 2>/dev/null || true

COPY animal-registry/src animal-registry/src
RUN mvn -pl animal-registry -am package -DskipTests -B -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN mkdir -p /app/uploads/animals
COPY --from=build /app/animal-registry/target/*.jar app.jar
EXPOSE 8002
ENTRYPOINT ["java", "-jar", "app.jar"]
