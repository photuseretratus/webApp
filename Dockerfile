FROM eclipse-temurin:11-jdk
CMD ["./gradlew", "clean", "assemble", "bootRun"]
COPY build/libs/webApp.jar app.jar
CMD ["java", "-jar", "app.jar"]
EXPOSE 8080