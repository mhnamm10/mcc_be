# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Runtime stage - Minimal for free tier
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

# Optimized JVM settings for small container (512MB limit recommended)
# -Xms64m: Start with 64MB heap
# -Xmx128m: Max 128MB heap (enough for minimal traffic)
# -XX:+UseG1GC: Good for small heaps
# -XX:+HeapDumpOnOutOfMemoryError: Debug OOM issues
# -Djava.security.egd=urandom: Better random for production
ENV JAVA_OPTS="-Xms64m -Xmx128m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -Djava.security.egd=urandom"
ENV SERVER_PORT=8080

# Graceful shutdown
STOPSIGNAL SIGTERM

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$SERVER_PORT -jar app.jar"]
