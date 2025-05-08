FROM eclipse-temurin:24-jdk-jammy as builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

RUN chmod +x gradlew
RUN ./gradlew build -x test

FROM eclipse-temurin:24-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]