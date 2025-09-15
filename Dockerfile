# Multi-stage build for Mythic Tales BMS (Taplist)

# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Leverage Docker layer caching by resolving deps first
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN mvn -q -DskipTests package

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy built jar
COPY --from=build /workspace/target/taplist-0.1.0.jar /app/app.jar

# Default runtime options (override with -e JAVA_OPTS="...")
ENV JAVA_OPTS=""

EXPOSE 8080
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]

