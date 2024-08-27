
FROM openjdk:11 AS stage1

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY src src
COPY build.gradle .
COPY settings.gradle .

RUN ./gradlew bootJar


FROM openjdk:11
WORKDIR /app
COPY --from=stage1 /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

#docker run -d -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:mariadb://host.docker.internal:3306/ordersystem ynlee557/ordersystem:latest
#docker run -d -p 8081:8080 -e SPRING_DATASOURCE_URL=jdbc:mariadb://host.docker.internal:3306/board -v C:\Users\Playdata\Desktop\tmp_logs:/app/logs spring_test:latest




