FROM --platform=linux/amd64 gradle:jdk17 as build_stage
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon --stacktrace

FROM --platform=linux/amd64 eclipse-temurin:17-jdk as package
RUN mkdir /webapp
COPY --from=build_stage /home/gradle/src/build/libs/*.jar /webapp/
EXPOSE 8081
CMD ["java", "-jar", "/webapp/webApp.jar", "--spring.config.location=/webapp/resources/application.yaml"]
