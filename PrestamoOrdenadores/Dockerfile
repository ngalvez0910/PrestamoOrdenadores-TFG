FROM openjdk:21-jdk-slim-buster as builder

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradlew gradlew.bat ./
COPY gradle ./gradle/
COPY src ./src/

RUN chmod +x gradlew

RUN ./gradlew bootJar

FROM openjdk:21-jdk-slim-buster

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar ./app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]