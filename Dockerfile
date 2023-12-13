FROM eclipse-temurin:11-jdk
CMD ["./gradlew", "clean", "assemble", "bootRun"]
COPY build/libs/webApp.jar app.jar
ENV PASSE_GOOGLE ""
ENV IMAGEKIT_PRIVATE_PASS ""
ENV IMAGEKIT_PUBLIC_PASS "public_TGL83sxiUWGZfYFL0MMz9r7AXTw="
ENV IMAGEKIT_URL "https://ik.imagekit.io/minecopre"
ENV SIGNATURE ""
ENV CIPHER_KEY ""
CMD ["java", "-jar", "app.jar"]
EXPOSE 8080