FROM gradle:8.5 as BUILD_STAGE
CMD ["gradle", "clean", "assemble"]
COPY build/libs/webApp.jar /webapp/app.jar
EXPOSE 8081
EXPOSE 587
ENTRYPOINT ["java", "-jar", "/webapp/app.jar"]