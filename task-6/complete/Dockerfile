FROM openjdk:17.0.1-jdk-slim
# 8080
WORKDIR .
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]