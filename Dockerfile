FROM gradle:7.5.0-jdk18 AS backend
COPY ./backend /content
WORKDIR /content
RUN gradle check shadowJar --no-daemon

FROM node:18 AS frontend
COPY ./frontend /content
WORKDIR /content
RUN npm run build

FROM openjdk:8-jre-slim
COPY --from=backend /content/build/libs/*-all.jar /app/server.jar
COPY --from=frontend /content/build /app/frontend

WORKDIR "/app"
ENTRYPOINT ["java", "-jar","server.jar"]