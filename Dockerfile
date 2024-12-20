FROM amazoncorretto:23-alpine
ARG frontend="./frontend/dist"
ARG backend="./backend/build/libs/backend.jar"

WORKDIR /app
COPY ${frontend} ./frontend
COPY ${backend} ./backend.jar
RUN ls -laR /app

ENTRYPOINT ["java", "-Djava.awt.headless=true", "-XX:MaxRAMPercentage=80", "-jar","backend.jar"]