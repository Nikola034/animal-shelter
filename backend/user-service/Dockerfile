FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY user-service/pom.xml user-service/pom.xml
COPY animal-registry/pom.xml animal-registry/pom.xml
COPY activity-tracking/pom.xml activity-tracking/pom.xml
COPY analytics-service/pom.xml analytics-service/pom.xml

RUN mvn -pl user-service -am dependency:go-offline -B -q 2>/dev/null || true

COPY user-service/src user-service/src
RUN mvn -pl user-service -am package -DskipTests -B -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/user-service/target/*.jar app.jar
EXPOSE 8001
ENTRYPOINT ["java", "-jar", "app.jar"]
