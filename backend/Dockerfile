# Build stage
FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY config config
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew build -x test -x checkstyleMain -x checkstyleTest

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp

# Create app user for security
RUN addgroup -g 1001 -S app && \
    adduser -u 1001 -S app -G app

# Copy the built jar file
COPY --from=build /workspace/app/build/libs/prompt-server-0.1.0.jar app.jar

# Change ownership to app user
RUN chown app:app app.jar

# Switch to app user
USER app

# Set JVM options for container
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
