FROM gradle:jdk17 as build_stage
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon --stacktrace

FROM eclipse-temurin:17-jdk as package
RUN mkdir /webapp
COPY --from=build_stage /home/gradle/src/build/libs/*.jar /webapp/webapp.jar
EXPOSE 8081
EXPOSE 587
ENTRYPOINT ["java", "-jar", "/webapp/webapp.jar", "--spring.config.location=/webapp/resources/application.yaml"]