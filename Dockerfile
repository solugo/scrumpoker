FROM gradle:7.5.0-jdk18 AS backend
COPY ./backend /content
WORKDIR /content
RUN gradle check shadowJar --no-daemon

FROM node:18 AS frontend
COPY ./frontend /content
WORKDIR /content
RUN npm install
RUN npm run build

FROM openjdk:17-alpine
COPY --from=backend /content/build/libs/*-all.jar /app/server.jar
COPY --from=frontend /content/build /app/frontend

WORKDIR "/app"
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-XX:MaxRAMPercentage=80", "-jar","server.jar"]