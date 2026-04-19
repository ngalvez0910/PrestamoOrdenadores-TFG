FROM eclipse-temurin:21-jdk as builder

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradlew gradlew.bat ./
COPY gradle ./gradle/
COPY src ./src/

RUN chmod +x gradlew

RUN ./gradlew bootJar

FROM eclipse-temurin:21-jdk

WORKDIR /app

RUN apt-get update && \
    apt-get install -y postgresql-client && \
    rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/build/libs/*.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/app/app.jar"]