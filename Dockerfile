FROM openjdk:11-jdk-slim
ARG JAR_FILE=*.jar
EXPOSE 8080
COPY ${JAR_FILE} app.jar
RUN mkdir /files
RUN mkdir /packaged
ENV ZIP_PATH=/packaged/
ENV TXT_PATH=/files/

ENTRYPOINT ["java","-jar","/app.jar"]
