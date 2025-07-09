FROM amazoncorretto:21-alpine
ARG frontend="./frontend/dist"
ARG backend="./backend/build/libs/backend.jar"

WORKDIR /app
COPY ${frontend} ./frontend
COPY ${backend} ./backend.jar

ENV SPRING_WEB_RESOURCES_STATIC_LOCATIONS ./frontend

ENTRYPOINT ["java", "-Djava.awt.headless=true", "-XX:MaxRAMPercentage=80", "-jar","backend.jar"]