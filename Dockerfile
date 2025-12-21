FROM eclipse-temurin:21-jdk

WORKDIR /app

# ARG JAR_FILE

# COPY ${JAR_FILE} app.jar

COPY target/task-aggregator-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "app.jar" ]