FROM maven:3.9.12-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn -B -pl backend/billing-service -am package -DskipTests
FROM eclipse-temurin:21.0.12.1_1-jre-alpine
RUN addgroup -S -g 10001 hospital && adduser -S -u 10001 hospital -G hospital
WORKDIR /app
COPY --from=build /build/backend/billing-service/target/billing-service-1.0.0.jar app.jar
USER 10001:10001
EXPOSE 8080
HEALTHCHECK --interval=20s --timeout=5s --start-period=90s --retries=6 CMD wget -qO- http://127.0.0.1:8080/actuator/health/readiness || exit 1
ENTRYPOINT ["java","-XX:MaxRAMPercentage=65","-jar","app.jar"]
