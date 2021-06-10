FROM openjdk:11-jdk
RUN useradd -ms /bin/bash temmink
USER temmink
WORKDIR /
ARG JAR_FILE=/target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
